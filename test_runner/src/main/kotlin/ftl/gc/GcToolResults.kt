package ftl.gc

import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.ToolResults
import com.google.api.services.toolresults.model.History
import com.google.api.services.toolresults.model.Step
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.credential
import ftl.config.FtlConstants.httpTransport

object GcToolResults {

    val service: ToolResults by lazy {
        val builder = ToolResults.Builder(httpTransport, JSON_FACTORY, credential)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.setApplicationName(applicationName)
            .build()
    }

    // https://github.com/bootstraponline/gcloud_cli/blob/0752e88b155a417a18d244c242b4ab3fb9aa1c1f/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/history_picker.py#L45
    private fun listHistoriesByName(args: IArgs): List<History> {
        val result = GcToolResults.service
            .projects()
            .histories()
            .list(args.projectId)
            .setFilterByName(args.resultsHistoryName)
            .execute()
        return result?.histories ?: emptyList()
    }

    private fun createHistory(args: IArgs): History {
        val history = History()
            .setName(args.resultsHistoryName)
            .setDisplayName(args.resultsHistoryName)
        return GcToolResults.service
            .projects()
            .histories()
            .create(args.projectId, history)
            .execute()
    }

    fun getHistoryId(args: IArgs): String? {
        if (args.resultsHistoryName == null) return null
        val histories = listHistoriesByName(args)
        if (histories.isNotEmpty()) return histories.first().historyId

        return createHistory(args).historyId
    }

    fun getResults(toolResultsStep: ToolResultsStep): Step {
        return GcToolResults.service
            .projects()
            .histories()
            .executions()
            .steps()
            .get(
                toolResultsStep.projectId,
                toolResultsStep.historyId,
                toolResultsStep.executionId,
                toolResultsStep.stepId
            )
            .execute()
    }

    fun getDefaultBucket(projectId: String): String? {
        val response = GcToolResults.service.Projects().initializeSettings(projectId).execute()
        return response.defaultBucket
    }
}
