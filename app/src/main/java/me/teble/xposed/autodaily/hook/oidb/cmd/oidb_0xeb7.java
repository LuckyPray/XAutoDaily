package me.teble.xposed.autodaily.hook.oidb.cmd;

import com.tencent.mobileqq.pb.MessageMicro;
import com.tencent.mobileqq.pb.PBEnumField;
import com.tencent.mobileqq.pb.PBField;
import com.tencent.mobileqq.pb.PBFloatField;
import com.tencent.mobileqq.pb.PBInt32Field;
import com.tencent.mobileqq.pb.PBInt64Field;
import com.tencent.mobileqq.pb.PBRepeatField;
import com.tencent.mobileqq.pb.PBRepeatMessageField;
import com.tencent.mobileqq.pb.PBStringField;

public final class oidb_0xeb7 {

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class ReqBody extends MessageMicro<ReqBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18}, new String[]{"signInStatusReq", "signInWriteReq"}, new Object[]{null, null}, ReqBody.class);
        public StSignInStatusReq signInStatusReq = new StSignInStatusReq();
        public StSignInWriteReq signInWriteReq = new StSignInWriteReq();
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class Ret extends MessageMicro<Ret> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 18}, new String[]{"code", "msg"}, new Object[]{0, ""}, Ret.class);
        public final PBEnumField code = PBField.initEnum(0);

        /* renamed from: msg  reason: collision with root package name */
        public final PBStringField f72972msg = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class RspBody extends MessageMicro<RspBody> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18}, new String[]{"signInStatusRsp", "signInWriteRsp"}, new Object[]{null, null}, RspBody.class);
        public StSignInStatusRsp signInStatusRsp = new StSignInStatusRsp();
        public StSignInWriteRsp signInWriteRsp = new StSignInWriteRsp();
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class SignInStatusBase extends MessageMicro<SignInStatusBase> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{8, 16}, new String[]{"status", "currentTimeStamp"}, new Object[]{0, 0L}, SignInStatusBase.class);
        public final PBEnumField status = PBField.initEnum(0);
        public final PBInt64Field currentTimeStamp = PBField.initInt64(0);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class SignInStatusDoneInfo extends MessageMicro<SignInStatusDoneInfo> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34}, new String[]{"leftTitleWrod", "rightDescWord", "belowPortraitWords", "recordUrl"}, new Object[]{"", "", "", ""}, SignInStatusDoneInfo.class);
        public final PBStringField leftTitleWrod = PBField.initString("");
        public final PBStringField rightDescWord = PBField.initString("");
        public final PBRepeatField<String> belowPortraitWords = PBField.initRepeat(PBStringField.__repeatHelper__);
        public final PBStringField recordUrl = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class SignInStatusGroupScore extends MessageMicro<SignInStatusGroupScore> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18}, new String[]{"groupScoreWord", "scoreUrl"}, new Object[]{"", ""}, SignInStatusGroupScore.class);
        public final PBStringField groupScoreWord = PBField.initString("");
        public final PBStringField scoreUrl = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class SignInStatusNotInfo extends MessageMicro<SignInStatusNotInfo> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26}, new String[]{"buttonWord", "signDescWordLeft", "signDescWordRight"}, new Object[]{"", "", ""}, SignInStatusNotInfo.class);
        public final PBStringField buttonWord = PBField.initString("");
        public final PBStringField signDescWordLeft = PBField.initString("");
        public final PBStringField signDescWordRight = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class SignInStatusYesterdayFirst extends MessageMicro<SignInStatusYesterdayFirst> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26}, new String[]{"yesterdayFirstUid", "yesterdayWord", "yesterdayNick"}, new Object[]{"", "", ""}, SignInStatusYesterdayFirst.class);
        public final PBStringField yesterdayFirstUid = PBField.initString("");
        public final PBStringField yesterdayWord = PBField.initString("");
        public final PBStringField yesterdayNick = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StDaySignedInfo extends MessageMicro<StDaySignedInfo> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 24, 32}, new String[]{"uid", "uidGroupNick", "signedTimeStamp", "signInRank"}, new Object[]{"", "", 0L, 0}, StDaySignedInfo.class);
        public final PBStringField uid = PBField.initString("");
        public final PBStringField uidGroupNick = PBField.initString("");
        public final PBInt64Field signedTimeStamp = PBField.initInt64(0);
        public final PBInt32Field signInRank = PBField.initInt32(0);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StDaySignedListReq extends MessageMicro<StDaySignedListReq> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 32, 40}, new String[]{"dayYmd", "uid", "groupId", "offset", "limit"}, new Object[]{"", "", "", 0, 0}, StDaySignedListReq.class);
        public final PBStringField dayYmd = PBField.initString("");
        public final PBStringField uid = PBField.initString("");
        public final PBStringField groupId = PBField.initString("");
        public final PBInt32Field offset = PBField.initInt32(0);
        public final PBInt32Field limit = PBField.initInt32(0);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StDaySignedListRsp extends MessageMicro<StDaySignedListRsp> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18}, new String[]{"ret", "page"}, new Object[]{null, null}, StDaySignedListRsp.class);
        public Ret ret = new Ret();
        public final PBRepeatMessageField<StDaySignedPage> page = PBField.initRepeatMessage(StDaySignedPage.class);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StDaySignedPage extends MessageMicro<StDaySignedPage> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 16, 24}, new String[]{"infos", "offset", "total"}, new Object[]{null, 0, 0}, StDaySignedPage.class);
        public final PBRepeatMessageField<StDaySignedInfo> infos = PBField.initRepeatMessage(StDaySignedInfo.class);
        public final PBInt32Field offset = PBField.initInt32(0);
        public final PBInt32Field total = PBField.initInt32(0);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StKingSignedInfo extends MessageMicro<StKingSignedInfo> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 24, 32}, new String[]{"uid", "groupNick", "signedTimeStamp", "signedCount"}, new Object[]{"", "", 0L, 0}, StKingSignedInfo.class);
        public final PBStringField uid = PBField.initString("");
        public final PBStringField groupNick = PBField.initString("");
        public final PBInt64Field signedTimeStamp = PBField.initInt64(0);
        public final PBInt32Field signedCount = PBField.initInt32(0);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StKingSignedListReq extends MessageMicro<StKingSignedListReq> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18}, new String[]{"uid", "groupId"}, new Object[]{"", ""}, StKingSignedListReq.class);
        public final PBStringField uid = PBField.initString("");
        public final PBStringField groupId = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StKingSignedListRsp extends MessageMicro<StKingSignedListRsp> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34}, new String[]{"ret", "yesterdayFirst", "topSignedTotal", "topSignedContinue"}, new Object[]{null, null, null, null}, StKingSignedListRsp.class);
        public Ret ret = new Ret();
        public StKingSignedInfo yesterdayFirst = new StKingSignedInfo();
        public final PBRepeatMessageField<StKingSignedInfo> topSignedTotal = PBField.initRepeatMessage(StKingSignedInfo.class);
        public final PBRepeatMessageField<StKingSignedInfo> topSignedContinue = PBField.initRepeatMessage(StKingSignedInfo.class);
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInRecordDaySigned extends MessageMicro<StSignInRecordDaySigned> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{13, 16, 26, 34}, new String[]{"daySignedRatio", "dayTotalSignedUid", "daySignedPage", "daySignedUrl"}, new Object[]{Float.valueOf(0.0f), 0, null, ""}, StSignInRecordDaySigned.class);
        public final PBFloatField daySignedRatio = PBField.initFloat(0.0f);
        public final PBInt32Field dayTotalSignedUid = PBField.initInt32(0);
        public StDaySignedPage daySignedPage = new StDaySignedPage();
        public final PBStringField daySignedUrl = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInRecordKing extends MessageMicro<StSignInRecordKing> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34}, new String[]{"yesterdayFirst", "topSignedTotal", "topSignedContinue", "kingUrl"}, new Object[]{null, null, null, ""}, StSignInRecordKing.class);
        public StKingSignedInfo yesterdayFirst = new StKingSignedInfo();
        public final PBRepeatMessageField<StKingSignedInfo> topSignedTotal = PBField.initRepeatMessage(StKingSignedInfo.class);
        public final PBRepeatMessageField<StKingSignedInfo> topSignedContinue = PBField.initRepeatMessage(StKingSignedInfo.class);
        public final PBStringField kingUrl = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInRecordReq extends MessageMicro<StSignInRecordReq> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26}, new String[]{"dayYmd", "uid", "groupId"}, new Object[]{"", "", ""}, StSignInRecordReq.class);
        public final PBStringField dayYmd = PBField.initString("");
        public final PBStringField uid = PBField.initString("");
        public final PBStringField groupId = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInRecordRsp extends MessageMicro<StSignInRecordRsp> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34, 42, 50}, new String[]{"ret", "base", "userRecord", "daySigned", "kingRecord", "level"}, new Object[]{null, null, null, null, null, null}, StSignInRecordRsp.class);
        public Ret ret = new Ret();
        public SignInStatusBase base = new SignInStatusBase();
        public StSignInRecordUser userRecord = new StSignInRecordUser();
        public StSignInRecordDaySigned daySigned = new StSignInRecordDaySigned();
        public StSignInRecordKing kingRecord = new StSignInRecordKing();
        public StViewGroupLevel level = new StViewGroupLevel();
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInRecordUser extends MessageMicro<StSignInRecordUser> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{16, 24, 32, 42, 50}, new String[]{"totalSignedDays", "earliestSignedTimeStamp", "continueSignedDays", "historySignedDays", "groupName"}, new Object[]{0, 0L, 0L, "", ""}, StSignInRecordUser.class);
        public final PBInt32Field totalSignedDays = PBField.initInt32(0);
        public final PBInt64Field earliestSignedTimeStamp = PBField.initInt64(0);
        public final PBInt64Field continueSignedDays = PBField.initInt64(0);
        public final PBRepeatField<String> historySignedDays = PBField.initRepeat(PBStringField.__repeatHelper__);
        public final PBStringField groupName = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInStatusReq extends MessageMicro<StSignInStatusReq> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 24, 34}, new String[]{"uid", "groupId", "scene", "clientVersion"}, new Object[]{"", "", 0, ""}, StSignInStatusReq.class);
        public final PBStringField uid = PBField.initString("");
        public final PBStringField groupId = PBField.initString("");
        public final PBEnumField scene = PBField.initEnum(0);
        public final PBStringField clientVersion = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInStatusRsp extends MessageMicro<StSignInStatusRsp> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26, 34, 42, 50, 58, 66}, new String[]{"ret", "base", "yesterday", "notInfo", "doneInfo", "groupScore", "mantleUrl", "backgroundUrl"}, new Object[]{null, null, null, null, null, null, "", ""}, StSignInStatusRsp.class);
        public Ret ret = new Ret();
        public SignInStatusBase base = new SignInStatusBase();
        public SignInStatusYesterdayFirst yesterday = new SignInStatusYesterdayFirst();
        public SignInStatusNotInfo notInfo = new SignInStatusNotInfo();
        public SignInStatusDoneInfo doneInfo = new SignInStatusDoneInfo();
        public SignInStatusGroupScore groupScore = new SignInStatusGroupScore();
        public final PBStringField mantleUrl = PBField.initString("");
        public final PBStringField backgroundUrl = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInWriteReq extends MessageMicro<StSignInWriteReq> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26}, new String[]{"uid", "groupId", "clientVersion"}, new Object[]{"", "", ""}, StSignInWriteReq.class);
        public final PBStringField uid = PBField.initString("");
        public final PBStringField groupId = PBField.initString("");
        public final PBStringField clientVersion = PBField.initString("");
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StSignInWriteRsp extends MessageMicro<StSignInWriteRsp> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18, 26}, new String[]{"ret", "doneInfo", "groupScore"}, new Object[]{null, null, null}, StSignInWriteRsp.class);
        public Ret ret = new Ret();
        public SignInStatusDoneInfo doneInfo = new SignInStatusDoneInfo();
        public SignInStatusGroupScore groupScore = new SignInStatusGroupScore();
    }

    /* compiled from: P */
    /* loaded from: classes23.dex */
    public static final class StViewGroupLevel extends MessageMicro<StViewGroupLevel> {
        static final MessageMicro.FieldMap __fieldMap__ = MessageMicro.initFieldMap(new int[]{10, 18}, new String[]{"title", "url"}, new Object[]{"", ""}, StViewGroupLevel.class);
        public final PBStringField title = PBField.initString("");
        public final PBStringField url = PBField.initString("");
    }

    private oidb_0xeb7() {
    }
}
