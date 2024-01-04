package com.example.organizationEmployeeProjectManagement.security.service.filterSpecification;

import com.example.organizationEmployeeProjectManagement.exception.NotFoundException;
import com.example.organizationEmployeeProjectManagement.security.dto.searchRequestDto.SearchRequest;
import com.example.organizationEmployeeProjectManagement.security.enums.specification.Operation;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.organizationEmployeeProjectManagement.security.enums.specification.Operator.LIKE;
import static java.nio.file.Paths.get;

/**
 * @author chandrika
 * @ProjectName demo-project-aprl-backend-2023
 * @since 25-04-2023
 */
@Service
public class FilterSpecification<EmployeeDto>{

    public Specification<EmployeeDto> getSearchSpecification(SearchRequest searchRequest){
        return new Specification<EmployeeDto>() {
            @Override
            public Predicate toPredicate(Root<EmployeeDto> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return criteriaBuilder.equal(root.get(searchRequest.getColumn().trim()), searchRequest.getValue().trim());
            }
        };
    }



    public Specification<EmployeeDto> getSearchSpecification(List<SearchRequest> searchRequest, Operation operation) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (SearchRequest request : searchRequest) {
                switch (request.getOperator()) {
                    case LIKE:
                        Predicate like = criteriaBuilder.like(root.get(request.getColumn().trim()),  request.getValue().trim() + "%");
                        predicates.add(like);
                        break;
                    case EQUAL:
                        Predicate equal = criteriaBuilder.equal(root.get(request.getColumn().trim()), request.getValue().trim());
                        predicates.add(equal);
                        break;
                    case IN:
                        String[] split=request.getValue().trim().split(",");
                        Predicate in=root.get(request.getColumn().trim()).in(Arrays.asList(split));
                        predicates.add(in);
                        break;
                    case GREATER_THAN:
                        Predicate greaterThan = criteriaBuilder.greaterThanOrEqualTo(root.get(request.getColumn().trim()), LocalDate.parse(request.getValue().trim()));
                        predicates.add(greaterThan);
                        break;
                    case LESS_THAN:
                        Predicate lessThan = criteriaBuilder.lessThanOrEqualTo(root.get(request.getColumn().trim()),LocalDate.parse(request.getValue().trim()));
                        predicates.add(lessThan);
                        break;
                    case BETWEEN:
                        String[] splitDate=request.getValue().trim().split(",");
                        Predicate between = criteriaBuilder.between(root.get(request.getColumn().trim()), LocalDate.parse(splitDate[0]), LocalDate.parse(splitDate[1]));
                        predicates.add(between);
                        break;
                    case JOIN:
Predicate join=criteriaBuilder.equal(root.join(request.getJoinTable()),get(request.getValue(), request.getColumn()));
                        predicates.add(join);
                        break;
                    default:
                        throw new NotFoundException("unexpected value :(");
                }
            }
            if (operation.equals(Operation.AND)) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            } else
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

}