package seth_k.app.brewday;

/**
 * Created by Seth on 2/24/2015.
 */

//For specifying the various forms of hops. Form can effect IBU conversion, needed amount, and
//absorption loss.
//    FRESH   Hops that are fresh cut from the vine (usually <24 hrs). Undried, so you need
//            2-3 times more by weight vs. the dried types.  All other forms are dried for storage.
//    WHOLE   Dried intact flowers.
//    PELLET  Dried; ground up and compressed into small pellets.
//    PLUG    Dried; ground up like pellets but larger in size.

public enum HopsForm {
    FRESH, WHOLE, PELLET, PLUG
}
