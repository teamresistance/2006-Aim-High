package frc.util;
/*
Author: Jim Hofmann
History: 
JCH - 11/8/2019 - Original Release

TODO: - Need more testing

Desc:
Can be used to set an object that toggles on each press.
*/

public class ToggleBool{
    private boolean toggleState;
    private boolean prvState;
    private boolean isFirstPass;

    public boolean Toggle( boolean inState ){
        if( inState != prvState ) {
            if( ! isFirstPass ) {
                toggleState = ! toggleState;
                isFirstPass = true;
            }else{
                isFirstPass = false;
            }
        }
        prvState = inState;
        return toggleState;
    }

    public boolean get(){
        return toggleState;
    }

    public void Release(){
        toggleState = false;
    }
}