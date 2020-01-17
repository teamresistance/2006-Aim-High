package frc.util;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;

public class BotMath{
    //------ Span methods -----------
    // span in between inLo & inHi to outLo & outHi
    // -- inLo must be lower than inHi
    // -- inDelta (inhi - inLo) MUST NOT BE 0. Returns outLo
    // -- outLo asso w/ inLo & outHi asso with inHi
    // -- clamp - limit between outLo & outHi
    // -- sqrt - apply sqrt root to input then span
    public static double Span(double inVal, double inLo, double inHi,
                                            double outLo, double outHi,
                                            boolean clamp, boolean sqrt){
        double tmp = (inVal - inLo) / (inHi - inLo);
        double outDelta = ( outHi - outLo );

        if( outDelta == 0.0) return outLo;

        if( sqrt ){
            tmp = ( tmp < 0 ? -1.0 : 1.0 ) * Math.sqrt( Math.abs( tmp )) * outDelta;
        }else{
            tmp = ( tmp * outDelta ) + outLo;
        }

        if( clamp ) tmp = Clamp(tmp, outLo, outHi );

        return tmp;
    }

    // inVal is limited between outLo & outHi
    public static double Clamp( double inVal, double outLo, double outHi){
        return Math.max( outLo, Math.min(outHi, inVal ));
    }

    // Left this here.  Does the same thing as clamp.
    public static boolean Between(double key, double min, double max) {
        return (key >= min && key <= max);
    }
    
    //---- Convert an arrray of booleans to an integer ----
    // No more than 15 items.  [0] is lsb.
    public static int Bool2Int( boolean arInBool[] ){
        int tmp = 0;
        int sizeAr = Math.min(arInBool.length, 15);
        for(int i = 0; i < sizeAr; i++){
            tmp = ( tmp << 1 ) + ( arInBool[i] ? 1 : 0 );   // Selection Operator
        }
        return tmp;
    }

    // Convert DI to int
    public static int DI2Int( DigitalInput arDI[] ){
        boolean arBool[] = new boolean[arDI.length];
        for( int i = 0; i < arDI.length; i++){
            arBool[i] = arDI[i].get();
        }
        return Bool2Int( arBool );
    }

    // Convert DO to int
    public static int DO2Int( DigitalOutput arDO[] ){
        boolean arBool[] = new boolean[arDO.length];
        for( int i = 0; i < arDO.length; i++){
            arBool[i] = arDO[i].get();
        }
        return Bool2Int( arBool );
    }
}