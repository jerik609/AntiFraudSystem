package antifraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

// interesting idea, but not needed atm

public class RepositoryUtils {

//    public static <S, T extends BaseEntity<S>> void saveIfNotExist(T baseEntity, JpaRepository<T, S> jpaRepository) {
//        jpaRepository
//                .findById(baseEntity.getId())
//                .orElseGet(() -> jpaRepository.save(baseEntity));
//    }

}
