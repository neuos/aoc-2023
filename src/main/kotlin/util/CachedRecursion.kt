package util

class CachedRecursion<Args, Res>(private val function: (rec: CachedRecursion<Args, Res>, Args) -> Res) : (Args) -> Res {
    private val cache: MutableMap<Args, Res> = mutableMapOf()
    override operator fun invoke(args: Args) = cache.getOrPut(args) { function(this, args) }
}
