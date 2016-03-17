package com.client.core.scheduledtasks.model.helper.impl;

import com.bullhornsdk.data.api.BullhornData;
import com.bullhornsdk.data.model.entity.core.standard.Candidate;
import com.bullhornsdk.data.model.entity.core.standard.CorporateUser;
import com.client.core.soap.model.SubscriptionEvent;
import com.client.core.scheduledtasks.model.helper.AbstractScheduledTaskHelper;

/**
 * Contains all the data needed to handle scheduled tasks automation. Once a  has been fetched using the BH api it
 * will be stored in this Traverser for subsequent automation work.
 * 
 * The allsToSaveBackToBH map will contain deep copies of relevant dtos that should be saved back to BH. The copies
 * will be updated according to task logic, while the original dtos will NOT be updated so that subsequent logic will
 * still be made on original values.
 * 
 * Once all automation work has been done the dtos that need saving will be saved only once. In this way keeping the api
 * calls to a minimum by saving each dto only once, even though multiple tasks might have updated different fields on
 * the same dto.
 * 
 * @author magnus.palm
 * 
 * @param <T>
 */
public class CandidateScheduledTaskHelper extends AbstractScheduledTaskHelper {

	private Candidate candidate;
	private CorporateUser candidateOwner;

	public CandidateScheduledTaskHelper(SubscriptionEvent event) {
		super(event);

	}

	public CandidateScheduledTaskHelper(SubscriptionEvent event, BullhornData bullhornData) {
		super(event,bullhornData);
	}

	/**
	 * Gets the Candidate for the event, if candidate == null then makes api call, otherwise returns candidate
	 * instance variable.
	 * 
	 * @return the Candidate connected to the event
	 */
	public Candidate getCandidate() {
		if (candidate == null) {
			setCandidate(findCandidate(getEvent().getEntityID()));
		}
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}

	/**
	 * Gets the CorporateUser candidate owner for the Candidate , if candidateOwner == null then makes api call,
	 * otherwise returns candidateOwner instance variable.
	 * 
	 * @return the CorporateUser connected to the candidate.
	 */
	public CorporateUser getCandidateOwner() {
		if (candidateOwner == null) {
			setCandidateOwner(findCorporateUser(getCandidate().getOwner().getId()));
		}
		return candidateOwner;
	}

	public void setCandidateOwner(CorporateUser candidateOwner) {
		this.candidateOwner = candidateOwner;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");
		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" candidate: " + candidate + NEW_LINE);
		result.append(" candidateOwner: " + candidateOwner + NEW_LINE);
		result.append("}");
		return result.toString();

	}

}