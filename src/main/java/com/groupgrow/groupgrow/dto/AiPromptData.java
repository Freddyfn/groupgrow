package com.groupgrow.groupgrow.dto;

import java.util.List;

public class AiPromptData {
    private GroupInfo groupInfo;
    private List<MemberInfo> membersInfo;

    // Constructors
    public AiPromptData() {
    }

    public AiPromptData(GroupInfo groupInfo, List<MemberInfo> membersInfo) {
        this.groupInfo = groupInfo;
        this.membersInfo = membersInfo;
    }

    // Getters and Setters
    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<MemberInfo> getMembersInfo() {
        return membersInfo;
    }

    public void setMembersInfo(List<MemberInfo> membersInfo) {
        this.membersInfo = membersInfo;
    }
}
