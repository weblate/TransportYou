package net.youapps.transport.components.directions

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.youapps.transport.R
import net.youapps.transport.TextUtils
import net.youapps.transport.data.transport.model.TripLeg

private const val MINIMUM_CHANGE_INTERVAL_MINUTES = 3

@Composable
fun TripLegIndividual(
    leg: TripLeg.Individual,
) {
    val durationMillis = leg.durationMillis
    // in case there's no time information available, we display the transfer as possible
    // because we don't have any info that speaks against it
    val isChangePossible = durationMillis == null || durationMillis > 0
    val isChangeShort =
        isChangePossible && durationMillis != null &&
                (durationMillis < MINIMUM_CHANGE_INTERVAL_MINUTES * 1000 ||
                        durationMillis < (leg.approxDurationMillis ?: 0))
    val changeColor =
        if (isChangePossible && !isChangeShort) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        individualIcons[leg.type]?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = changeColor
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column {
            val changeText =
                if (isChangePossible) stringResource(individualNames[leg.type]!!)
                else stringResource(R.string.transfer_impossible)
            Text(
                text = changeText + durationMillis?.takeIf { isChangePossible }?.let {
                    " (${TextUtils.prettifyDurationLongText(it)})"
                }
                    .orEmpty(),
                color = changeColor
            )

            leg.distance?.takeIf { it != 0 }?.let { distanceMeters ->
                Text(
                    text = TextUtils.formatDistance(distanceMeters) + leg.approxDurationMillis?.let {
                        ", ${stringResource(R.string.approximately_abbr)} ${
                            TextUtils.prettifyDurationShortText(it)
                        }"
                    },
                    color = changeColor
                )
            }
        }
    }
}