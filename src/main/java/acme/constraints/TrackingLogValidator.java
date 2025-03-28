
package acme.constraints;

import java.util.Date;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.SpringHelper;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.entities.trackingLog.TrackingLogRepository;

public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		System.out.println(trackingLog);
		if (!trackingLog.getPublished() || trackingLog.getClaim() == null)
			return true;

		TrackingLogRepository trackingLogRepository = SpringHelper.getBean(TrackingLogRepository.class);
		List<TrackingLog> trackingLogs = trackingLogRepository.findPublishedTrackingLogsByClaimId(trackingLog.getClaim().getId());

		System.out.println(trackingLogs);

		boolean percentageIncreases = true;
		if (!trackingLogs.isEmpty()) {
			TrackingLog lastTrackingLog = trackingLogs.get(trackingLogs.size() - 1);
			percentageIncreases = trackingLog.getResolutionPercentage() > lastTrackingLog.getResolutionPercentage();
		}

		boolean indicatorMatchesPercentage = trackingLog.getResolutionPercentage() == 100.0
			? trackingLog.getIndicator() == TrackingLogIndicator.ACCEPTED || trackingLog.getIndicator() == TrackingLogIndicator.REJECTED || trackingLog.getIndicator() == TrackingLogIndicator.IN_REVIEW
			: trackingLog.getIndicator() == TrackingLogIndicator.PENDING;

		boolean resolutionIsValid = trackingLog.getIndicator() == TrackingLogIndicator.ACCEPTED || trackingLog.getIndicator() == TrackingLogIndicator.REJECTED || trackingLog.getIndicator() == TrackingLogIndicator.IN_REVIEW
			? !trackingLog.getResolution().isBlank()
			: true;

		boolean isFirstAndNotInReview = trackingLogs.isEmpty() ? trackingLog.getIndicator() != TrackingLogIndicator.IN_REVIEW : true;

		boolean dateIsValid = true;
		Date registrationMoment = trackingLog.getClaim().getRegistrationMoment();
		Date trackingLogLastUpdateMoment = trackingLog.getLastUpdateMoment();
		if (!trackingLogs.isEmpty()) {
			Date lastMoment = trackingLogs.get(trackingLogs.size() - 1).getLastUpdateMoment();
			dateIsValid = !trackingLogLastUpdateMoment.before(lastMoment);
		} else
			dateIsValid = !trackingLogLastUpdateMoment.before(registrationMoment);

		boolean claimIsPublished = trackingLog.getClaim().getPublished();

		return percentageIncreases && indicatorMatchesPercentage && resolutionIsValid && isFirstAndNotInReview && dateIsValid && claimIsPublished;
	}

}
