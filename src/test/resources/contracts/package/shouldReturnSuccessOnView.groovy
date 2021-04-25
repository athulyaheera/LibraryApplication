import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should return even when number input is even"
    request{
        method GET()
        url("/library/viewBooks") {
        }
    }
    response {
        status 200
    }
}