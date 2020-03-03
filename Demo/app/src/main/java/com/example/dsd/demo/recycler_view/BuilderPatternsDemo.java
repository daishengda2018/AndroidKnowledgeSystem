package com.example.dsd.demo.recycler_view;

/**
 * 构造器模式
 *
 * 此模式
 * Created by im_dsd on 2019-12-19
 */
public class BuilderPatternsDemo {
    private final String mFirstName;
    private final String mLastName;
    private final String mGender;
    private final int mAge;

    private BuilderPatternsDemo(Builder builder) {
        mFirstName = builder.mFirstName;
        mLastName = builder.mLastName;
        mGender = builder.mGender;
        mAge = builder.mAge;
    }

    /**
     *
     */
    public static final class Builder {
        private String mFirstName;
        private String mLastName;
        private String mGender;
        private int mAge;

        public Builder() {
        }

        public Builder setMFirstName(String val) {
            mFirstName = val;
            return this;
        }

        public Builder setMLastName(String val) {
            mLastName = val;
            return this;
        }

        public Builder setMGender(String val) {
            mGender = val;
            return this;
        }

        public Builder setMAge(int val) {
            mAge = val;
            return this;
        }

        public BuilderPatternsDemo build() {
            return new BuilderPatternsDemo(this);
        }
    }
}
