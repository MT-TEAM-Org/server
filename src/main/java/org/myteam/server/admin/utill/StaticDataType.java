package org.myteam.server.admin.utill;

public enum StaticDataType {


    Report, ReportedChat, ReportedComment, ReportedBoard,
    //BOARD,COMMENT의 경우 ReportType이라는 enum과 값을 맞춰야 하는 로직이 다른 기능에서 존재해서 대문자로 했습니다.
    BOARD, COMMENT, BoardComment,

    Improvement, Inquiry, ImprovementInquiry,

    UserAccess, UserSignIn, UserDeleted, UserWarned, UserBanned,

    HideComment, HideBoard
}
