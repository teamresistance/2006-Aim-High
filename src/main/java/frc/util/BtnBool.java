package frc.util;
/*
Author: Jim Hofmann
History: 
JCH - 11/8/2019 - Original Release

TODO: - Check it out.

Desc:
Can be used to set a wait timer
*/

public class BtnBool {
    
	private boolean previousState = false;
	
    //Need to figure out how to pass by reference.  Otherwise a these calls don't make sense.
    //Could just do it locally, using boolean.
	public BtnBool() {
        //nothing here
	}

	public boolean isDown(boolean currentState) {
		return currentState;
	}	
	
	public boolean isUp(boolean currentState) {
		return !currentState;
	}	
	
	public boolean onButtonPressed( boolean currentState) {
        if(currentState != previousState){
            previousState =  currentState;
            return true;
        }else{
            return false;
        }
	}
	
	public boolean onButtonReleased(boolean currentState) {
        if(!currentState != previousState){
            previousState =  currentState;
            return true;
        }else{
            return false;
        }
	}
}


