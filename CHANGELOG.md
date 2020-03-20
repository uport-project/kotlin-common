* 0.4.3
    * fix - upgrade assertk & apply detekt config ( #11 )
    
* 0.4.2
    * fix - Expose internal dependencies as API to fix NoClassDefFound errors downstream ( 791c0aa7 )

* 0.4.1
    * fix - okhttp dependency ( 4ac0e8d1 )( 4f2839f4 )
    
* 0.4.0
    * feat - easy configuration with infuraProjectID ( #8 )
    * feat - find networks by name after configuration ( #9 )

* 0.3.2
    * build - bump kethereum to 0.76.2 ( 61d7582d )

* 0.3.1
    * refactor - simplify imports, using lowercase coordinates for komputing libs ( 6a827d05 )
    
* 0.3.0
    * refactor - replace moshi with kotlinx.serialization ( #6 )
    * feature - add ethChainId  ( f658925f )
    * feature - throw more specific JsonRpcInvalidArgumentException ( a79b3a01 )
    
* 0.2.0
    * update to kethereum 0.76.1 [#5](https://github.com/uport-project/kotlin-common/pull/5)
    * [breaking] isolate implementation details for each module (#5)
        * may require adjusting imports
    * removed all direct references to java classes in production code (#5)

* 0.1.2
    * maintenance - add test coverage
    
* 0.1.1
    * maintenance - fix a bad test
    
* 0.1.0
    * isolated signer-common module and library.
    * Signer import coordinates have changed to `me.uport.sdk.signer` 
    * using gradle 5.4.1 - requires more explicit dependency resolution
    * started using CI for automatic testing

* 0.0.5
    * prune dependency tree - more lightweight libs
    
* 0.0.4
    * using JDK 1.8 as source and compile target
    
* 0.0.3
    * bump kethereum dependency to 0.75.0
    
* 0.0.2
    * add Signer definitions and basic implementation to core
    
* 0.0.1
    * initial release, contains `core`, `jsonrpc`, `test-helpers` modules