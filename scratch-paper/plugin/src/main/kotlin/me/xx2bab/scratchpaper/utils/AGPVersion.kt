package me.xx2bab.scratchpaper.utils


class AGPVersion(var version: String) : Comparable<AGPVersion> {

    init {
        // Any alpha/beta/rc version we deem it as the formal one
        if (version.contains("-")) {
            val indexOfDash = version.indexOf("-")
            version = version.substring(0, indexOfDash)
        }
        // should only include
        if (!version.matches("[0-9]+(\\.[0-9]+)*".toRegex())) {
            throw IllegalArgumentException("Invalid version format")
        }
    }

    override fun compareTo(other: AGPVersion): Int {
        val thisParts = this.get().split("\\.".toRegex())
        val thatParts = other.get().split("\\.".toRegex())
        val length = Math.max(thisParts.size, thatParts.size)
        for (i in 0 until length) {
            val thisPart = if (i < thisParts.size)
                Integer.parseInt(thisParts[i])
            else
                0
            val thatPart = if (i < thatParts.size)
                Integer.parseInt(thatParts[i])
            else
                0
            if (thisPart < thatPart) {
                return -1
            }
            if (thisPart > thatPart) {
                return 1
            }
        }
        return 0
    }

    fun get(): String {
        return version
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AGPVersion

        if (this != other) return false

        return true
    }

    override fun hashCode(): Int {
        return version.hashCode()
    }

}