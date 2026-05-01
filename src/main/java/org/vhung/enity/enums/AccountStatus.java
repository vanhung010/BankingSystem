package org.vhung.enity.enums;

import org.vhung.enity.Account;

public enum AccountStatus {
    ACTIVE {
        @Override
        public void handle(){

        }
    },
    LOCKED {
        @Override
        public void handle(){
            throw new RuntimeException("Tài khoản đang bị khóa, vui lòng liên hệ nhân viên để mở!");
        }
    },
    CLOSED {
        @Override
        public void handle(){
            throw new RuntimeException("Tài khoản đã đóng, không thể thực hiện");
        }
    };
    public abstract void handle();
}
