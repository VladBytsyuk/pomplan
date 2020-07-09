package com.vbytsyuk.pomodoro.jvm

object Sizes {
    const val SCALING_FACTOR = 2.0

    object Design {
        object Button {
            object Small {
                const val SIZE = 64.0 * SCALING_FACTOR
            }
            object Big {
                const val SIZE = 96.0 * SCALING_FACTOR
            }
        }
    }

    object Window {
        const val WIDTH = 360.0 * SCALING_FACTOR
        const val HEIGHT = 520.0 * SCALING_FACTOR
    }

    object Pomodoro {
        object Progress {
            const val SIZE = 256.0 * SCALING_FACTOR
        }
        object Buttons {
            const val WIDTH = 256.0 * SCALING_FACTOR
            const val HEIGHT = 96.0 * SCALING_FACTOR
        }
    }
}
