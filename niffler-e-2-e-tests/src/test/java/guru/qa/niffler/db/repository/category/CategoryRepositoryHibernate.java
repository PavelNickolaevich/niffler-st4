package guru.qa.niffler.db.repository.category;

import guru.qa.niffler.db.EmfProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.CategoryEntity;

import static guru.qa.niffler.db.Database.SPEND;

public class CategoryRepositoryHibernate extends JpaService implements CategoryRepository {

    public CategoryRepositoryHibernate() {
        super(SPEND, EmfProvider.INSTANCE.emf(SPEND).createEntityManager());
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        persist(SPEND, categoryEntity);
        return categoryEntity;
    }
}
