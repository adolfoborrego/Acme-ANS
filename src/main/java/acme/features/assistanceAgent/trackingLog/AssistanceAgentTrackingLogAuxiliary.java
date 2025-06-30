
package acme.features.assistanceAgent.trackingLog;

import java.util.ArrayList;
import java.util.List;

import acme.client.components.views.SelectChoices;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLogIndicator;

public class AssistanceAgentTrackingLogAuxiliary {

	public static SelectChoices getPossibleIndicatorChoices(final Claim claim, final TrackingLogIndicator selected, final boolean isCreate) {
		SelectChoices result = new SelectChoices();
		result.add("0", "----", selected == null);

		TrackingLogIndicator current = claim.getIndicator();

		if (current == TrackingLogIndicator.PENDING) {
			result.add(TrackingLogIndicator.PENDING.name(), "PENDING", TrackingLogIndicator.PENDING.equals(selected));
			result.add(TrackingLogIndicator.ACCEPTED.name(), "ACCEPTED", TrackingLogIndicator.ACCEPTED.equals(selected));
			result.add(TrackingLogIndicator.REJECTED.name(), "REJECTED", TrackingLogIndicator.REJECTED.equals(selected));
		} else if (current == TrackingLogIndicator.ACCEPTED || current == TrackingLogIndicator.REJECTED)
			result.add(TrackingLogIndicator.IN_REVIEW.name(), "IN_REVIEW", TrackingLogIndicator.IN_REVIEW.equals(selected));
		else
			result.add(current.name(), current.name(), current.equals(selected));

		if (!isCreate && selected != null && !result.hasChoiceWithKey(selected.name()))
			result.add(selected.name(), selected.name(), true);

		return result;
	}

	public static List<TrackingLogIndicator> getLegalIndicators(final Claim claim) {
		List<TrackingLogIndicator> legalIndicators = new ArrayList<TrackingLogIndicator>();
		legalIndicators.add(null);
		if (claim.getIndicator() == TrackingLogIndicator.PENDING) {
			legalIndicators.add(TrackingLogIndicator.PENDING);
			legalIndicators.add(TrackingLogIndicator.ACCEPTED);
			legalIndicators.add(TrackingLogIndicator.REJECTED);
		} else if (claim.getIndicator() == TrackingLogIndicator.ACCEPTED || claim.getIndicator() == TrackingLogIndicator.REJECTED)
			legalIndicators.add(TrackingLogIndicator.IN_REVIEW);
		return legalIndicators;
	}
}
