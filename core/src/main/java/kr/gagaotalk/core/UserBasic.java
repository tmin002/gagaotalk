package kr.gagaotalk.core;
import java.util.Date;

public class UserBasic {
    public final String ID;
    public String nickname;
    public String bio;
    public Date birthDay;

    public UserBasic(String ID, String nickname, String bio, Date birthday) {
        this.ID = ID;
        this.nickname = nickname;
        this.bio = bio;
        this.birthDay = birthday;
    }
}
