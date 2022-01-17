# Backend for cloud opener API

run it with:
    
    rm build/generated/ -fr &&  ./gradlew clean bootRun

A cloudlink can be created:

    curl -XPOST -H"Accept: application/json"  \
        http://localhost:8080/v1/cloudlink -v

Then can be polled:

    curl -XGET -H"Accept: application/json"  \
        http://localhost:8080/v1/cloudlink/${cloudlinkCode} -v 

until it has an exchange in its response,
then it can stop polling the cloudlink.

Exchange is created:
    
    curl -XPOST -H"Accept: application/json"  \
        http://localhost:8080/v1/exchange -v

Then the exchange is connected with a cloudlink,
by patching the cloudlink:
    
    curl -XPATCH -H"Accept: Content-Type/json" \
        -d"{ exchangeHandle: \"${exchangeHandle}\"}" \
        http://localhost:8080/v1/cloudlink -v
        
Exchange can be polled with:

    curl -XGET -H"Accept: application/json"  \
        http://localhost:8080/v1/exchange/${exhchangeHandle}?from=${timestamp} -v 

or even (Not for the MVP):

    curl -XGET -H"Accept: text/html"  \
        http://localhost:8080/v1/exchange/${exhchangeHandle}?from=${timestamp} -v 

New messages can be posted to exchange with:

    curl -XPOST -H"Accept: Content-Type/json"  \
        -d"{$messageBody}"
        http://localhost:8080/v1/exchange/${exhchangeHandle}/messages -v

where the messageBody is something like:

    {
        type: "text/plain",
        body: "hello, how are you?"
        timestamp: 1643565582
    }
