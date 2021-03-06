package ftl.gc

import com.dd.plist.NSDictionary
import com.google.api.services.testing.model.IosDeviceList
import ftl.args.IosArgs
import ftl.test.util.FlankTestRunner
import ftl.util.ShardCounter
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class GcIosTestMatrixTest {

    @Test(expected = IllegalArgumentException::class)
    fun build_negativeShardErrors() {
        val iosArgs = mock(IosArgs::class.java)
        `when`(iosArgs.testShardChunks).thenReturn(listOf(listOf("")))
        GcIosTestMatrix.build(
            IosDeviceList(), "", "", -1, NSDictionary(), iosArgs, ShardCounter()
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun build_invalidShardErrors() {
        val iosArgs = mock(IosArgs::class.java)
        GcIosTestMatrix.build(
            IosDeviceList(), "", "", 1, NSDictionary(), iosArgs, ShardCounter()
        )
    }

    @Test
    fun build_validArgs() {
        val iosArgs = mock(IosArgs::class.java)
        `when`(iosArgs.testShardChunks).thenReturn(listOf(listOf("")))
        `when`(iosArgs.testTimeout).thenReturn("3m")
        `when`(iosArgs.resultsBucket).thenReturn("/hi")
        `when`(iosArgs.projectId).thenReturn("123")
        `when`(iosArgs.xctestrunFile).thenReturn("456")

        GcIosTestMatrix.build(
            IosDeviceList(), "", "", 0, NSDictionary(), iosArgs, ShardCounter()
        )
    }
}
