# uPort kotlin-common

[![](https://jitpack.io/v/uport-project/kotlin-common.svg)](https://jitpack.io/#uport-project/kotlin-common)
[![CircleCI](https://circleci.com/gh/uport-project/kotlin-common.svg?style=svg)](https://circleci.com/gh/uport-project/kotlin-common)

Core interface definitions and default implementations for uPort kotlin SDK classes.

[FAQ and helpdesk support](http://bit.ly/uPort_helpdesk)

## Modules

### core

##### Contents
    * definitions for the `HttpClient` used by all SDK methods,
    * extension methods for dealing with base64,
    * `ITimeProvider` and default implementation (`SystemTimeProvider`)
    * `Networks` object and defaults for mainnet and the most used test networks
##### Usage
```kotlin

//use defaults
val registryAddress = Networks.mainnet.ethrDidRegistry //"0xdca7ef03e98e0dc2b855be647c39abe984fcf21b"

// register a testnet
Networks.registerNetwork(
    EthNetwork(
        name = "local",
        networkId = "0x1234",
        rpcUrl = "http://localhost:8545",
        ethrDidRegistry = "0xdca7ef03e98e0dc2b855be647c39abe984fcf21b"
    )
)

//register all popular public networks using your infura project ID
Networks.registerAllNetworksWithInfura("<YOUR INFURA ID>")

//use testnet
val net = Networks.get("0x1234")
or
val net = Networks.get("local")

// override defaults
Networks.registerNetwork(
    Networks.mainnet.copy(rpcUrl = "http://localhost:8545")
)
assertTrue(Networks.mainnet.rpcUrl == "http://localhost:8545")

```

### jsonrpc
##### Contents
 [JSON RPC method](https://github.com/ethereum/wiki/wiki/JSON-RPC) wrappers used in the uPort SDK.
 This is not a complete set of methods and is meant to be internal. Use at your own risk.

### signer-common
#### Contents
    * `Signer` interface definition
    * `KPSigner` default implementation. A `Signer` that uses a private key that lives in memory.
    * extensions to provide coroutine functionality over callbacks
    and to juggle with key and signature encodings
#### Usage
```kotlin

val signer :Signer = KPSigner("0x65fc670d9351cb87d1f56702fb56a7832ae2aab3427be944ab8c9f2a0ab87960")
//suspended call
val sigData = signer.signJWT("<some serialized JWT payload>")
val signatureString = sigData.getJoseEncoded()
```
### test-helpers
#### Contents
Methods and extensions to ease testing with uPort SDK.
`coAssert` extension for [assertk](https://github.com/willowtreeapps/assertk)
`isInstanceOf` for multiple classes extension for assertk
`TestTimeProvider` usable to juggle with time in JWT tests

## Installation

These libraries are available through [jitpack](https://jitpack.io/)

In your main `build.gradle` file, add:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
        //...
    }
}
```

In your module `build.gradle` file, add:

```groovy
def uport_kotlin_common_version = "0.4.1"
dependencies {
    //...
    // core lib
    implementation "com.github.uport-project.kotlin-common:core:$uport_kotlin_common_version"
    //signer-common lib
    implementation "com.github.uport-project.kotlin-common:signer-common:$uport_kotlin_common_version"
}
```

*NOTE*
while jitpack allows you to import the whole distribution like so:
 `implementation "com.github.uport-project:kotlin-common:$uport_kotlin_common_version"`
 it is NOT a good idea to import the whole distribution because it will 
 bring `assertk` into your runtime and bloat your app/library.


## Credits

This SDK depends on
* several modules of [KEthereum](https://github.com/komputing/KEthereum)
* [spongycastle](https://rtyley.github.io/spongycastle/) - for base64 and DER encodings
* [okhttp](https://github.com/square/okhttp) - for http client
* [kmnid](https://github.com/uport-project/kmnid) - for safer encodings of eth addresses
* [assertk](https://github.com/willowtreeapps/assertk) used in testing
