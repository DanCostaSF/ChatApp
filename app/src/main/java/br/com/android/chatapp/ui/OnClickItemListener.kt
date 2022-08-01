package br.com.android.chatapp.ui

interface OnClickItemListener {
    fun <T, I>onItemClick(item: T, intent: I? = null )
}