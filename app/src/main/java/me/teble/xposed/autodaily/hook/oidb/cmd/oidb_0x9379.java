package me.teble.xposed.autodaily.hook.oidb.cmd;

import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBInt32Field;

public final class oidb_0x9379 {
    private oidb_0x9379() {
    }

    public static final class ReqBody extends MessageMicro<ReqBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8}, new String[]{"taskType"}, new Object[]{0}, ReqBody.class);
        public final PBInt32Field taskType = PBField.initInt32(0);
    }

    public static final class RspBody extends MessageMicro<RspBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{}, new String[]{}, new Object[]{}, RspBody.class);
    }
}
