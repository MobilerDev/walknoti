package com.cansevin.walknoti

enum class DirectionActionType(val type: String) {
    STRAIGHT("straight"),
    TURN_LEFT("turn-left"),
    TURN_RIGHT("turn-right"),
    TURN_SLIGHT_LEFT("turn-slight-left"),
    TURN_SLIGHT_RIGHT("turn-slight-right"),
    TURN_SHARP_LEFT("turn-sharp-left"),
    TURN_SHARP_RIGHT("turn-sharp-right"),
    UTURN_LEFT("uturn-left"),
    UTURN_RIGHT("uturn-right"),
    RAMP_LEFT("ramp-left"),
    RAMP_RIGHT("ramp-right"),
    MERGE("merge"),
    FORK_LEFT("fork-left"),
    FORK_RIGHT("fork-right"),
    FERRY ("ferry"),
    FERRY_TRAIN ("ferry-train"),
    ROUNDABOUT_LEFT("roundabout-left"),
    ROUNDABOUT_RIGHT("roundabout-right"),
    END("end"),
    UNKOWN("unknown")
}
