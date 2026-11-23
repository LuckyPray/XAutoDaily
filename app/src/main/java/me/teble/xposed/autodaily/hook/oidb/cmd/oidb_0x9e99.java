package me.teble.xposed.autodaily.hook.oidb.cmd;

import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBBoolField;
import com.tencent.mobileqq.pb.PBEnumField;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBInt32Field;

public final class oidb_0x9e99 {
    private oidb_0x9e99() {
    }

    public static final class ReqBody extends MessageMicro<ReqBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8}, new String[]{"scene"}, new Object[]{0}, ReqBody.class);
        public final PBEnumField scene = PBField.initEnum(0);
    }

    public static final class RspBody extends MessageMicro<RspBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16, 24, 32, 40, 56, 64}, new String[]{"isSVip", "today", "cycleStart", "currentDay", "status", "cardNum", "missedDays"}, new Object[]{false, 0, 0, 0, 0, 0, 0}, RspBody.class);
        public final PBBoolField isSVip = PBField.initBool(false);
        public final PBInt32Field today = PBField.initInt32(0);
        public final PBInt32Field cycleStart = PBField.initInt32(0);
        public final PBInt32Field currentDay = PBField.initInt32(0);
        public final PBEnumField status = PBField.initEnum(0);
        public final PBInt32Field cardNum = PBField.initInt32(0);
        public final PBInt32Field missedDays = PBField.initInt32(0);
    }
}
