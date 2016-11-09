package com.firstry.gmase.quantilife.model

/**
 * Created by Guille2 on 22/08/2016.
 */

/*
states:
1 yes
-1 no
0 unknown
 */
class Tag(val tagId: String, val YesPhrase: String = "", val NoPhrase: String = "", var day: Int = 0, var state: Int = 0) {

}