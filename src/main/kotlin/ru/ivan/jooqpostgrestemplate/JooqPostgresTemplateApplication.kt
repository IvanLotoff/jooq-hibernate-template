package ru.ivan.jooqpostgrestemplate

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.*
import ru.ivan.domain.tables.Cities.Companion
import ru.ivan.domain.tables.Cities.Companion.CITIES
import ru.ivan.domain.tables.pojos.Cities

@SpringBootApplication
class JooqPostgresTemplateApplication

fun main(args: Array<String>) {
	runApplication<JooqPostgresTemplateApplication>(*args)
}

@RestController
@RequestMapping("/api")
class CityController {
	@Autowired
	private lateinit var cityRepository: CityRepository

	@GetMapping("/all")
	fun findAll(): List<Cities> = cityRepository.findAll()

	@GetMapping("/{id}")
	fun findById(@PathVariable("id") id: Long) = cityRepository.findById(id)

	@PostMapping("/save")
	fun save(@RequestBody city: Cities) = cityRepository.save(city)

	@DeleteMapping("/{id}")
	fun deleteById(@PathVariable("id") id: Long) = cityRepository.deleteById(id)
}

interface CityRepository {
	fun findAll(): List<Cities>
	fun findById(id: Long): Cities?
	fun save(city: Cities)
	fun deleteById(id: Long)
}

@Repository
class CityRepositoryImpl: CityRepository {
	@Autowired
	private lateinit var dsl: DSLContext

	override fun findAll(): List<Cities> {
		return dsl.select()
			.from(CITIES)
			.fetchInto(Cities::class.java)
	}

	override fun findById(id: Long): Cities? {
		return dsl.select()
			.from(CITIES)
			.where(CITIES.ID.eq(id))
			.fetchOneInto(Cities::class.java)
	}

	override fun save(city: Cities) {
		dsl.insertInto(
			CITIES,
			CITIES.COUNTRY_ID,
			CITIES.NAME
		)
			.values(
				city.countryId,
				city.name
			)
			.execute()
	}

	override fun deleteById(id: Long) {
		dsl.deleteFrom(CITIES)
			.where(CITIES.ID.eq(id))
			.execute()
	}

}

