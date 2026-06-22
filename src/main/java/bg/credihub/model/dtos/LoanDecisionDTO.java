package bg.credihub.model.dtos;

import bg.credihub.model.enums.DecisionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDecisionDTO {
    @NotNull(message = "Decision status is required.")
    private DecisionStatus status;
    @NotBlank(message = "Decision comment is required.")
    private String comment;
}
