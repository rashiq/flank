package ftl.gc

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.util.Utils.sleep
import java.time.Duration.ofHours

object GcTestMatrix {

    // Getting the test matrix may throw an internal server error.
    //  {
    //      "code" : 500,
    //      "errors" : [ {
    //      "domain" : "global",
    //      "message" : "Internal error encountered.",
    //      "reason" : "backendError"
    //  } ],
    //      "message" : "Internal error encountered.",
    //      "status" : "INTERNAL"
    //  }
    //
    // Randomly throws errors... yay FTL
    //    com.google.api.client.googleapis.json.GoogleJsonResponseException: 503 Service Unavailable
    //    {
    //        "code" : 503,
    //        "errors" : [ {
    //        "domain" : "global",
    //        "message" : "The service is currently unavailable.",
    //        "reason" : "backendError"
    //    } ],
    //        "message" : "The service is currently unavailable.",
    //        "status" : "UNAVAILABLE"
    //    }
    fun refresh(testMatrixId: String, config: IArgs): TestMatrix {
        val getMatrix = GcTesting.get.projects().testMatrices().get(config.projectId, testMatrixId)
        var failed = 0
        val maxWait = ofHours(1).seconds

        while (failed < maxWait) {
            try {
                return getMatrix.execute()
            } catch (e: Exception) {
                sleep(1)
                failed += 1
            }
        }

        throw RuntimeException("Failed to refresh matrix")
    }
}
