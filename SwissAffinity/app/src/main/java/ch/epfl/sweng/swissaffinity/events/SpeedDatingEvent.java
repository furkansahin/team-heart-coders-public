package ch.epfl.sweng.swissaffinity.events;

import java.io.Serializable;
import java.util.Objects;


/**
 * Representation of a speed dating event.
 */
public class SpeedDatingEvent extends Event implements Serializable {

    private final int mMenSeats;
    private final int mWomenSeats;
    private final int mMenRegistered;
    private final int mWomenRegistered;
    private final int mMinAge;
    private final int mMaxAge;
    private final Establishment mEstablishment;

    private SpeedDatingEvent(Builder builder) {
        super(builder);
        this.mMenSeats = builder.mMenSeats;
        this.mWomenSeats = builder.mWomenSeats;
        this.mMenRegistered = builder.mMenRegistered;
        this.mWomenRegistered = builder.mWomenRegistered;
        this.mMinAge = builder.mMinAge;
        this.mMaxAge = builder.mMaxAge;
        this.mEstablishment = builder.mEstablishment;
    }

    /**
     * Getter for men seats
     *
     * @return the number of men seats
     */
    public int getMenSeats() {
        return mMenSeats;
    }

    /**
     * Getter for women seats
     *
     * @return the number of women seats
     */
    public int getWomenSeats() {
        return mWomenSeats;
    }

    /**
     * Getter for the regidtered men
     *
     * @return the number of registered men
     */
    public int getMenRegistered() {
        return mMenRegistered;
    }

    /**
     * Getter for the registered women
     *
     * @return the number of registered women
     */
    public int getWomenRegistered() {
        return mWomenRegistered;
    }

    /**
     * Getter for the maximum age
     *
     * @return the maximum age for the event
     */
    public int getMaxAge() {
        return mMaxAge;
    }

    /**
     * Getter for the minimum age
     *
     * @return the minimum age for the event
     */
    public int getMinAge() {
        return mMinAge;
    }

    /**
     * Getter for the establishment
     *
     * @return the establishment for the event
     */
    public Establishment getEstablishment() {
        return mEstablishment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SpeedDatingEvent that = (SpeedDatingEvent) o;
        return Objects.equals(mMenSeats, that.mMenSeats) &&
               Objects.equals(mWomenSeats, that.mWomenSeats) &&
               Objects.equals(mMenRegistered, that.mMenRegistered) &&
               Objects.equals(mWomenRegistered, that.mWomenRegistered) &&
               Objects.equals(mMinAge, that.mMinAge) &&
               Objects.equals(mMaxAge, that.mMaxAge) &&
               Objects.equals(mEstablishment, that.mEstablishment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            mMenSeats,
            mWomenSeats,
            mMenRegistered,
            mWomenRegistered,
            mMinAge,
            mMaxAge,
            mEstablishment);
    }

    /**
     * Builder for a speed dating event.
     */
    public static class Builder extends Event.Builder {

        private int mMenSeats;
        private int mWomenSeats;
        private int mMenRegistered;
        private int mWomenRegistered;
        private int mMinAge;
        private int mMaxAge;
        private Establishment mEstablishment;

        /**
         * Setter for men seats
         *
         * @param menSeats the number of men seats
         * @return this
         */
        public Builder setMenSeats(int menSeats) {
            if (menSeats < 0) {
                throw new IllegalArgumentException();
            }
            mMenSeats = menSeats;
            return this;
        }

        /**
         * Setter for women seats
         *
         * @param womenSeats the number of women seats
         * @return this
         */
        public Builder setWomenSeats(int womenSeats) {
            if (womenSeats < 0) {
                throw new IllegalArgumentException();
            }
            mWomenSeats = womenSeats;
            return this;
        }

        /**
         * Setter for registered men
         *
         * @param menRegistered the number of registered men
         * @return this
         */
        public Builder setMenRegistered(int menRegistered) {
            if (menRegistered < 0) {
                throw new IllegalArgumentException();
            }
            mMenRegistered = menRegistered;
            return this;
        }

        /**
         * Setter for registered women
         *
         * @param womenRegistered the number of registered women
         * @return this
         */
        public Builder setWomenRegistered(int womenRegistered) {
            if (womenRegistered < 0) {
                throw new IllegalArgumentException();
            }
            mWomenRegistered = womenRegistered;
            return this;

        }

        /**
         * Setter for minimum age
         *
         * @param minAge the minimum age
         * @return this
         */
        public Builder setMinAge(int minAge) {
            if (minAge < 0 ||
                (mMaxAge > 0 && minAge > mMaxAge))
            {
                throw new IllegalArgumentException();
            }

            mMinAge = minAge;
            return this;
        }

        /**
         * Setter for maximum age
         *
         * @param maxAge the maximum age
         * @return this
         */
        public Builder setMaxAge(int maxAge) {
            if (maxAge < 0 ||
                (mMinAge > 0 && mMinAge > maxAge))
            {
                throw new IllegalArgumentException();
            }
            mMaxAge = maxAge;
            return this;
        }

        /**
         * Setter for establishment
         *
         * @param establishment the establishment {@link Establishment}
         * @return this
         */
        public Builder setEstablishment(Establishment establishment) {
            if (establishment == null) {
                throw new IllegalArgumentException();
            }
            mEstablishment = establishment;
            return this;
        }

        /**
         * Build the speed dating event
         *
         * @return the speed dating event {@link SpeedDatingEvent}
         */
        public SpeedDatingEvent build() {
            return new SpeedDatingEvent(this);
        }
    }
}
