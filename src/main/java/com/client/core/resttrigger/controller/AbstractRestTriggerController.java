package com.client.core.resttrigger.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.bullhornsdk.data.api.BullhornData;
import com.bullhornsdk.data.model.entity.core.type.BullhornEntity;
import com.client.core.AppContext;
import com.client.core.base.controller.AbstractTriggerController;
import com.client.core.base.tools.entitychanger.EntityChanger;
import com.client.core.base.tools.web.JsonConverter;
import com.client.core.base.util.TriggerUtil;
import com.client.core.base.workflow.node.Node;
import com.client.core.base.workflow.traversing.AbstractTriggerTraverser;
import com.client.core.resttrigger.model.api.RestTriggerRequest;
import com.client.core.resttrigger.model.api.RestTriggerResponse;

/**
 * Created by hiqbal on 12/15/2015.
 */

public class AbstractRestTriggerController<T extends BullhornEntity, TR extends AbstractTriggerTraverser<T, ?>> extends AbstractTriggerController<T, TR> {

    private final Class<T> type;
    private final Node<TR> validationWorkflow;

    protected final BullhornData bullhornData;

    private final JsonConverter jsonConverter;
	private final EntityChanger entityChanger;

    private static Logger log = Logger.getLogger(AbstractRestTriggerController.class);

    public AbstractRestTriggerController(Class<T> type, Node<TR> validationWorkflow) {
        super();
        this.type = type;
        this.validationWorkflow = validationWorkflow;
        this.bullhornData = AppContext.getApplicationContext().getBean(BullhornData.class);
        this.jsonConverter = AppContext.getApplicationContext().getBean(JsonConverter.class);
	    this.entityChanger = AppContext.getApplicationContext().getBean(EntityChanger.class);
    }

    protected RestTriggerRequest<T> convertToObject(String value) {
        return jsonConverter.convertJsonStringToEntity(value, RestTriggerRequest.class, type);
    }

    protected Map<String, Object> convertToMap(String value) {
        return jsonConverter.convertJsonStringToMap(value);
    }

    /**
     * Helper method for handling the request
     *
     * @param traverser
     * @return the json parsed validation message
     */
    protected RestTriggerResponse handleRequest(TR traverser, Map<String, Object> entity) {
        try {
            validationWorkflow.start(traverser);
        } catch (Exception e) {
            log.error("Error validating Entity", e);

            return prepareErrorReturnValue(entity);
        }

        return prepareReturnValue(traverser, entity);
    }

    protected RestTriggerResponse prepareErrorReturnValue(Map<String, Object> fields){
        RestTriggerResponse restTriggerResponse = new RestTriggerResponse();

        restTriggerResponse.setResult(false);
        restTriggerResponse.setError("Error saving Entity. Please try again.");
        restTriggerResponse.setEntity(fields);

        return restTriggerResponse;
    }

	protected RestTriggerResponse prepareReturnValue(TR validationTraverser, Map<String, Object> entity){
		RestTriggerResponse restTriggerResponse = new RestTriggerResponse();

		StringBuilder error = new StringBuilder();

		for (Map.Entry<String, Object> entry : validationTraverser.getFormResponse().entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			// if error
			if (TriggerUtil.isError(key)) {
				error.append(value + "</br>");
			}
			// if return value
			if (TriggerUtil.isReturnValue(key)) {
				key = key.substring(12);

				entity.put(key, value);
			}
		}

		if(StringUtils.isEmpty(error.toString())){
			restTriggerResponse.setResult(true);
		} else{
			restTriggerResponse.setResult(false);
			restTriggerResponse.setError(error.toString());
		}

		restTriggerResponse.setEntity(entity);

		return restTriggerResponse;
	}

}