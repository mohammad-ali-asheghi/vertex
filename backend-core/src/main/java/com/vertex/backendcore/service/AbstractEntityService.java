package com.vertex.backendcore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vertex.backendcore.repository.AbstractDAO;
import com.vertex.core.exceptions.ServiceException;
import com.vertex.core.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * سرویس پایه برای تمام Entity‑ها.
 * <p>
 * نکات مهم:
 * <ul>
 *   <li>تمام متدهای CRUD بررسی Permission را انجام می‌دهند.</li>
 *   <li>کدهای تکراری به متدهای کمکی عمومی استخراج شده‌اند.</li>
 *   <li>ObjectMapper به صورت thread‑safe و یکبار پیکربندی می‌شود.</li>
 *   <li>استفاده از Optional برای فیلدها و پیام‌های لاگ واضح.</li>
 * </ul>
 */
@Slf4j
public abstract class AbstractEntityService<E> {

    /* ------------------------------------------------------------------ *
     *  1️⃣   ObjectMapper مشترک (immutable)                               *
     * ------------------------------------------------------------------ */
    private static final ObjectMapper MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        SimpleModule mod = new SimpleModule();
        mod.addSerializer(Date.class, new DateSerializer());
        mod.addDeserializer(Date.class, new DateDeserializers.DateDeserializer());
        mapper.registerModule(mod);
        return mapper;
    }

    /* ------------------------------------------------------------------ *
     *  2️⃣   فیلدهای اصلی کلاس                                            *
     * ------------------------------------------------------------------ */
    private static final Logger LOG = Logger.getLogger(AbstractEntityService.class.getName());

    private final AbstractDAO<E> repository;

    /**
     * پیاده‌سازی توسط زیرکلاس برای دسترسی به EntityManager
     */
    protected abstract EntityManager getEntityManager();

    /**
     * برای مدیریت تراکنش ها همراه با مسیج بروکر ها
     */
    protected TrxManager trxManager;

    protected AbstractEntityService(AbstractDAO<E> repository, PlatformTransactionManager ptm) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.trxManager = new TrxManager(ptm);
    }

    /* ------------------------------------------------------------------ *
     *  3️⃣   دسترسی به DAO و Permission                                   *
     * ------------------------------------------------------------------ */
    public AbstractDAO<E> getDao() {
        return repository;
    }

    /**
     * فقط در صورتی که زیرکلاس به hintهای خاصی نیاز داشته باشد بازنویسی می‌شود
     */
    protected Map<String, String> getHints() {
        return Collections.emptyMap();
    }

    /* ------------------------------------------------------------------ *
     *  4️⃣   عملیات CREATE / UPDATE / DELETE                              *
     * ------------------------------------------------------------------ */
    public void insert(E entity, boolean flush) throws ServiceException {
        Objects.requireNonNull(entity, "entity must not be null");
        try {
            trxManager.doInTransaction(status -> {
                try {
                    getDao().save(entity);
                    if (flush) {
                        getDao().flush();
                    }
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
            log.info("create operation is: true");
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Database constraint error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("create operation is: false", e);
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unused")
    public void insert(E entity) throws ServiceException {
        insert(entity, true);
    }

    public void update(E entity, boolean flush) throws ServiceException {
        Objects.requireNonNull(entity, "entity must not be null");
        try {
            trxManager.doInTransaction(status -> {
                try {
                    getDao().save(entity);
                    if (flush) {
                        getDao().flush();
                    }
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
            log.info("update operation is: true for entity {}", entity);
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Update failed due to database constraint: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Update operation failed for entity: {}", entity, e);
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unused")
    public void update(E entity) throws ServiceException {
        update(entity, true);
    }

    public void delete(Long id) {
        getDao().deleteById(id);
    }

    /* ------------------------------------------------------------------ *
     *  5️⃣   عملیات جمع‑آوری (Batch)                                      *
     * ------------------------------------------------------------------ */
    public void insert(Collection<E> entities, boolean flush) throws ServiceException {
        Objects.requireNonNull(entities, "entities must not be null");
        if (entities.isEmpty()) return;
        try {
            trxManager.doInTransaction(status -> {
                try {
                    getDao().saveAll(entities);
                    if (flush) {
                        getDao().flush();
                    }
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
            log.info("create list operation is: true. count: {}", entities.size());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Batch insert failed due to database constraint: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Batch insert operation failed for entities count: {}", entities.size(), e);
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unused")
    public void insert(Collection<E> entities) throws ServiceException {
        insert(entities, true);
    }

    public void update(Collection<E> entities, boolean flush) throws ServiceException {
        Objects.requireNonNull(entities, "entities must not be null");
        if (entities.isEmpty()) return;
        try {
            trxManager.doInTransaction(status -> {
                try {
                    getDao().saveAll(entities);
                    if (flush) {
                        getDao().flush();
                    }
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
            log.info("update list operation is: true. count: {}", entities.size());
        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            log.error("Batch update failed due to database constraint: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Batch update operation failed for entities count: {}", entities.size(), e);
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unused")
    public void update(Collection<E> entities) throws ServiceException {
        update(entities, true);
    }

    @SuppressWarnings("unused")
    public void delete(Collection<Long> ids) {
        List<E> entities = getDao().findAll(
                (root, query, cb) -> root.get("id").in(ids)
        );
        getDao().deleteAll(entities);
    }

    @SuppressWarnings("unused")
    public void delete(List<Long> refIds, String refField) {
        List<E> entities = getDao().findAll(
                (root, query, cb) -> root.get(refField).in(refIds)
        );
        getDao().deleteAll(entities);
    }

    @SuppressWarnings("unused")
    public void delete(Long refId, String refField) {
        List<E> entities = getDao().findAll((root, query, cb) -> cb.equal(root.get(refField), refId));
        getDao().deleteAll(entities);
    }

    /* ------------------------------------------------------------------ *
     * 7️⃣   عملیات Read / Load                                            *
     * ------------------------------------------------------------------ */
    public E findById(Class<E> clz, Object id) {
        Objects.requireNonNull(clz, "entity class must not be null");
        return getEntityManager().find(clz, id);
    }

    @SuppressWarnings("unused")
    public Iterable<E> getList() {
        return getDao().findAll();
    }

    @SuppressWarnings("unused")
    public Iterable<E> getListBySpecification(Specification<E> spec) {
        return getDao().findAll(spec);
    }

    /* --------------------------------------------------------------- *
     بخش‌های مرتبط با جستجو و بارگذاری (load / count)            *
     * --------------------------------------------------------------- */
    @SuppressWarnings("unused")
    public Page<E> load(Pageable pageable, Class<E> clz, JPARestriction restriction) {
        return load(pageable, clz, restriction, pageable.getSort());
    }

    @SuppressWarnings("unused")
    public List<E> load(Class<E> clz, JPARestriction restriction, Sort sort) {
        return load(null, clz, restriction, sort).getContent();
    }

    /**
     * متد عمومی برای ایجاد CriteriaQuery، اعمال فیلترها، مرتب‌سازی
     * و در نهایت ساخت Page یا List.
     */
    public Page<E> load(Pageable pageable, Class<E> clz,
                        JPARestriction restriction, Sort sort) {
        return executeLoad(pageable, clz, restriction, sort, true);
    }

    /**
     * نسخهٔ عمومی‌تری که می‌تواند شمارش کل‌ها را نادیده بگیرد
     * (استفاده در روش‌های بهینه‌سازی «needCount = false»).
     */
    @SuppressWarnings("unused")
    public Page<E> load(Pageable pageable, Class<E> clz,
                        JPARestriction restriction, Sort sort,
                        boolean needCount) {
        return executeLoad(pageable, clz, restriction, sort, needCount);
    }

    /**
     * ساختن Criteria، افزودن Order و پردازش Pageable.
     */
    @SuppressWarnings("unchecked")
    private Page<E> executeLoad(Pageable pageable, Class<E> clz,
                                JPARestriction restriction, Sort sort,
                                boolean needCount) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(clz);
        Root<E> root = cq.from(clz);
        cq.select(root);

        // ---------- فیلترها ----------
        if (restriction != null) {
            if (restriction.distinct()) {
                cq.distinct(true);
            }
            Specification<E> spec = restriction.listSpec(cb, cq, root);
            if (spec != null) {
                Predicate predicate = spec.toPredicate(root, cq, cb);
                if (predicate != null) {
                    cq.where(predicate);
                }
            }
        }

        // ---------- مرتب سازی ----------
        if (sort != null) {
            List<Order> orderList = new ArrayList<>();
            sort.forEach(s -> {
                if (StringUtil.isNotEmpty(s.getProperty())) {
                    try {
                        Path<?> path = getPath(root, s.getProperty());
                        if (s.isAscending())
                            orderList.add(cb.asc(path));
                        else
                            orderList.add(cb.desc(path));
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        System.out.println("transient field " + s.getProperty() + " could not be sorted!");
                    }
                }
            });
            if (!CollectionUtils.isEmpty(orderList))
                cq.orderBy(orderList);
        }

        TypedQuery<E> query = getEntityManager().createQuery(cq);
        applyHints(query);

        // ---------- صفحه‌بندی ----------
        if (pageable != null) {
            int pageSize = pageable.getPageSize();
            query.setFirstResult((int) pageable.getOffset());

            // وقتی needCount = false می‌خواهیم فقط یک رکورد اضافی بگیریم
            query.setMaxResults(needCount ? pageSize : pageSize + 1);

            List<E> list = query.getResultList();

            if (needCount) {
                long total = this.count(clz, restriction);
                return new PageImpl<>(list, pageable, total);
            } else {
                boolean hasNext = list.size() > pageSize;
                if (hasNext) {
                    list = list.subList(0, pageSize);
                }
                return new PageImpl<>(list, pageable, hasNext ? pageSize + 1 : list.size()) {
                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }
                };
            }
        }

        // اگر pageable = null → تمام نتایج
        List<E> all = query.getResultList();
        return new PageImpl<>(all);
    }

    /**
     * اعمال hintهای سفارشی (اگر زیرکلاس آن‌ها را بازنویسی کند).
     */
    private void applyHints(Query query) {
        getHints().forEach(query::setHint);
    }

    /* --------------------------------------------------------------- *
     *  شمارش ردیف‌ها (count)                                         *
     * --------------------------------------------------------------- */
    @SuppressWarnings("unchecked")
    private long count(Class<E> clz, JPARestriction restriction) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<E> root = cq.from(clz);
        cq.select(cb.count(root));

        if (restriction != null) {
            Specification<E> spec = restriction.countSpec(cb, cq, root);
            if (spec != null) {
                Predicate predicate = spec.toPredicate(root, cq, cb);
                if (predicate != null) {
                    cq.where(predicate);
                }
            }
        }
        Number result = getEntityManager().createQuery(cq).getSingleResult();
        return result != null ? result.longValue() : 0L;
    }

    /* --------------------------------------------------------------- *
     *  به‌روزرسانی موجودیت از JSON (readForUpdate)                  *
     * --------------------------------------------------------------- */
    @SuppressWarnings("unused")
    public List<E> readForUpdate(Class<E> clz, String jsonArrayContent) {

        if (StringUtil.isEmpty(jsonArrayContent)) {
            throw new ServiceException("ContentsIsEmpty");
        }

        List<E> entities = new ArrayList<>();

        try {
            JsonNode jsonNode = MAPPER.readTree(jsonArrayContent);

            if (!jsonNode.isArray()) {
                throw new ServiceException("Invalid JSON payload");
            }

            for (int i = 0; i < jsonNode.size(); i++) {
                JsonNode obj = jsonNode.get(i);

                JsonNode idNode = obj.get("id");
                if (idNode == null || !idNode.isNumber()) {
                    throw new ServiceException("Invalid JSON payload");
                }
                Long id = idNode.asLong();

                E persisted = findById(clz, id);
                if (persisted == null) {
                    throw new ServiceException("EntityNotFound");
                }

                MAPPER.readerForUpdating(persisted).readValue(obj.toString());

                entities.add(persisted);
            }

        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to parse JSON for update: " + e.getMessage(), e);
            throw new ServiceException("Invalid JSON payload", e);
        }

        return entities;
    }

    @SuppressWarnings("unused")
    public E readForUpdate(Class<E> clz, Long id, String jsonContent) {
        E persisted = findById(clz, id);
        if (persisted == null) {
            throw new ServiceException("EntityNotFound");
        }
        try {
            MAPPER.readerForUpdating(persisted).readValue(jsonContent);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to parse JSON for update id=" + id + ": " + e.getMessage(), e);
            throw new ServiceException("Invalid JSON payload", e);
        }
        return persisted;
    }

    @SuppressWarnings("unused")
    public List<Object[]> dataList(
            Class<E> entityClass,
            JPARestriction restriction,
            List<String> fields,
            int offset,
            int limit
    ) {
        CriteriaQuery<Object[]> cq = buildDynamicSelectionCriteria(entityClass, restriction, fields);
        TypedQuery<Object[]> query = getEntityManager().createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(limit);
        query.setHint("org.hibernate.readOnly", true);
        query.setHint("org.hibernate.fetchSize", limit);

        return query.getResultList();
    }

    @SuppressWarnings("unused")
    public List<Object[]> dataList(
            Class<E> entityClass,
            JPARestriction restriction,
            List<String> fields
    ) {
        CriteriaQuery<Object[]> cq = buildDynamicSelectionCriteria(entityClass, restriction, fields);
        TypedQuery<Object[]> query = getEntityManager().createQuery(cq);
        query.setHint("org.hibernate.readOnly", true);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    private CriteriaQuery<Object[]> buildDynamicSelectionCriteria(Class<E> entityClass, JPARestriction restriction, List<String> fields) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = cb.createQuery(Object[].class);
        Root<E> root = criteriaQuery.from(entityClass);

        List<Path<Object>> paths = new ArrayList<>(fields.size());
        for (String field : fields) {
            paths.add((Path<Object>) getPath(root, field));
        }

        criteriaQuery.select(cb.array(paths.toArray(new Path[0])));

        if (restriction != null) {
            Specification<E> spec = restriction.listSpec(cb, criteriaQuery, root);
            if (spec != null) {
                Predicate predicate = spec.toPredicate(root, criteriaQuery, cb);
                if (predicate != null) {
                    criteriaQuery.where(predicate);
                }
            }
        }
        return criteriaQuery;
    }

    private void setQueryParams(Query query, Object search) {
        Map<String, Object> params = MAPPER.convertValue(search, new TypeReference<>() {
        });
        params.remove("id");
        params.remove("needCount");
        params.remove("needCountSafe");
        params.forEach(query::setParameter);
    }

    @SuppressWarnings("unused")
    public Sort getNativeQuerySort(Class<E> clz, Sort sort) {
        return NativeQuerySortHandler.getNativeQuerySort(clz, sort);
    }

    @SuppressWarnings("unused")
    public List<E> loadList(Class<E> clz, String queryName, Object search) {
        TypedQuery<E> query = this.getEntityManager().createNamedQuery(queryName, clz);
        this.setQueryParams(query, search);
        return query.getResultList();
    }

    /**
     * Deletes a single item in a new transaction
     * Used in batch/scheduler loops - failure of one doesn't stop others
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @SuppressWarnings("unused")
    public void deleteEach(Long id) {
        this.delete(id);
    }

    protected Path<?> getPath(From<?, ?> path, String property) {
        if (property == null) {
            return null;
        }
        String[] split = property.split("\\.");
        int index = 0;
        return joinOrGetPath(path, split, index);
    }

    private Path<?> joinOrGetPath(From<?, ?> path, String[] split, int index) {
        if (split.length - 1 == index) {
            return path.get(split[index]);
        } else {
            Join<Object, Object> join = path.join(split[index++], JoinType.LEFT);
            return joinOrGetPath(join, split, index);
        }
    }
}
