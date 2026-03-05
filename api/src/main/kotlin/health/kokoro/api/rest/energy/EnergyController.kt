package health.kokoro.api.rest.energy

import health.kokoro.application.usecase.energy.GetEnergyPercentage
import health.kokoro.domain.model.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/energy")
class EnergyController(
    private val getEnergyAmount: GetEnergyPercentage
){
    @GetMapping
    fun getEnergyInfo(): ResponseEntity<EnergyInfoDto> {
       val user = SecurityContextHolder.getContext().authentication.principal as User
       return ResponseEntity.ok(
           EnergyInfoDto(getEnergyAmount.execute(user.id!!))
       )
    }
}