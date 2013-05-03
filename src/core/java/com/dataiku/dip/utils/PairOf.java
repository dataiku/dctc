package com.dataiku.dip.utils;

public class PairOf<U> {
        public final U first;
        public final U second;

        public PairOf(U first, U second) {
                this.first = first;
                this.second = second;
        }
}
