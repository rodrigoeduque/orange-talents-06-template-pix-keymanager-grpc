package br.com.zup.rodrigoeduque

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.rodrigoeduque")
		.start()
}

