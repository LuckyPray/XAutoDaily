package me.teble.xposed.autodaily.hook.oidb.cmd;

import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBInt32Field;
import com.tencent.mobileqq.pb.PBStringField;

public final class oidb_0x9de7 {
    private oidb_0x9de7() {
    }

    public static final class ReqBody extends MessageMicro<ReqBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16, 26}, new String[]{"version", "type", "json"}, new Object[]{0, 0, ""}, ReqBody.class);
        public final PBInt32Field version = PBField.initInt32(0);
        public final PBInt32Field type = PBField.initInt32(0);
        public final PBStringField json = PBField.initString("");
    }

    public static final class RspBody extends MessageMicro<RspBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16, 24, 34}, new String[]{"version", "type", "ret", "json"}, new Object[]{0, 0, 0, ""}, RspBody.class);
        public final PBInt32Field version = PBField.initInt32(0);
        public final PBInt32Field type = PBField.initInt32(0);
        public final PBInt32Field ret = PBField.initInt32(0);
        public final PBStringField json = PBField.initString("");
    }
}
