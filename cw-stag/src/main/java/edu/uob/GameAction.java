package edu.uob;

import edu.uob.GameEntities.GameEntity;

import java.util.ArrayList;

public class GameAction
{
    ArrayList<String> triggers;
    ArrayList<String> subjects;
    ArrayList<String> consumed;
    ArrayList<String> produced;
    String narration;

    public GameAction(){

    }

}


//    <action>
//        <triggers>
//            <keyphrase>open</keyphrase>
//            <keyphrase>unlock</keyphrase>
//        </triggers>
//        <subjects>
//            <entity>trapdoor</entity>
//            <entity>key</entity>
//        </subjects>
//        <consumed>
//            <entity>key</entity>
//        </consumed>
//        <produced>
//            <entity>cellar</entity>
//        </produced>
//<narration>You unlock the trapdoor and see steps leading down into a cellar</narration>
//    </action>