package com.example.myapplication

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GridPagingSource : PagingSource<Int, Int>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
        val page = params.key ?: 0
        val pageSize = 5 // 5 rows per page

        val startRow = page * pageSize
        val endRow = startRow + pageSize

        // Cap the total rows at 50 for this example
        if (startRow >= 50) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        val rows = (startRow until minOf(endRow, 50)).toList()

        return LoadResult.Page(
            data = rows,
            prevKey = if (page == 0) null else page - 1,
            nextKey = if (rows.isEmpty()) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Int>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}