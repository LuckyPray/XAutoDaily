package me.teble.xposed.autodaily.hook.oidb.cmd;

import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBBoolField;
import com.tencent.mobileqq.pb.PBEnumField;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBInt32Field;
import com.tencent.mobileqq.pb.PBRepeatMessageField;
import com.tencent.mobileqq.pb.PBStringField;

public final class oidb_0x9e9a {
    private oidb_0x9e9a() {
    }

    public static final class ReqBody extends MessageMicro<ReqBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16}, new String[]{"scene", "action"}, new Object[]{0, 0}, ReqBody.class);
        public final PBEnumField scene = PBField.initEnum(0);
        public final PBEnumField action = PBField.initEnum(0);
    }

    public static final class RspBody extends MessageMicro<RspBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16, 26, 32, 40, 48, 56}, new String[]{"result", "currentDay", "rewards", "cardNum", "cardGranted", "missedDays", "canGuideAutoPay"}, new Object[]{0, 0, null, 0, false, 0, false}, RspBody.class);
        public final PBEnumField result = PBField.initEnum(0);
        public final PBInt32Field currentDay = PBField.initInt32(0);
        public final PBRepeatMessageField<Reward> rewards = PBField.initRepeatMessage(Reward.class);
        public final PBInt32Field cardNum = PBField.initInt32(0);
        public final PBBoolField cardGranted = PBField.initBool(false);
        public final PBInt32Field missedDays = PBField.initInt32(0);
        public final PBBoolField canGuideAutoPay = PBField.initBool(false);
    }

    public static final class Reward extends MessageMicro<Reward> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16, 26, 34}, new String[]{"type", "num", "name", "icon"}, new Object[]{0, 0, "", ""}, Reward.class);
        public final PBInt32Field type = PBField.initInt32(0);
        public final PBInt32Field num = PBField.initInt32(0);
        public final PBStringField name = PBField.initString("");
        public final PBStringField icon = PBField.initString("");
    }
}
