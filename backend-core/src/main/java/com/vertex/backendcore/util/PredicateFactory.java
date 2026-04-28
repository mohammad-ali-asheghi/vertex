package com.vertex.backendcore.util;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * A factory class for creating and managing predicates, which are boolean-valued functions used in querying and filtering data.
 * This class provides a fluent API for building complex predicates based on various conditions.
 */
@SuppressWarnings("unused")
public class PredicateFactory {

    /**
     * A nested class that acts as a builder for creating predicates. It allows for the construction of predicates
     * in a step-by-step manner, using method chaining for a more readable and concise syntax.
     */
    public static class PredicateBuilder {
        private final List<Predicate> predicates;
        private final Root<?> root;
        private final CriteriaBuilder criteriaBuilder;

        /**
         * Constructs a new PredicateBuilder instance.
         *
         * @param root            The root of the query, representing the entity being queried.
         * @param criteriaBuilder The CriteriaBuilder instance used to construct the predicates.
         */
        private PredicateBuilder(Root<?> root, CriteriaBuilder criteriaBuilder) {
            this.root = root;
            this.criteriaBuilder = criteriaBuilder;
            predicates = new ArrayList<>();
        }

        /**
         * Adds a predicate to the builder to ensuring that the specified attribute is null.
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .isNull("personName");}
         * </pre>
         * In this example, the method is used to filter results where the "personName" attribute is null.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder isNull(String attributeName) {
            predicates.add(criteriaBuilder.isNull(root.get(attributeName)));

            return this;
        }

        /**
         * Adds a predicate to the builder to ensuring that the specified attribute is NOT null.
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .isNotNull("personName");}
         * </pre>
         * In this example, the method is used to filter results where the "personName" attribute is NOT null.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder isNotNull(String attributeName) {
            predicates.add(criteriaBuilder.isNotNull(root.get(attributeName)));

            return this;
        }

        /**
         * Adds a predicate to the builder for matching strings that contains a specified value.
         * This method is case-insensitive and is useful for filtering results based on if string attribute contains a specified value.
         * <p>Note: When the supplied value is null or an empty string, no predicate will be added to the builder.
         * The supplied value will be converted to uppercase using {@code String.toUpperCase()} </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .stringContains("personName", model.getPersonNameFilter());}
         * </pre>
         * In this example, the method is used to filter results where the "personName" attribute contains the value provided by {@code model.getPersonNameFilter()}.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should start with. This method will perform a case-insensitive search.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder stringContains(String attributeName, String value) {
            if (value != null && !value.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(attributeName)), "%" + value.toUpperCase() + "%"));
            }

            return this;
        }

        /**
         * Adds a predicate to the builder for matching strings that start with a specified value.
         * This method is case-insensitive and is useful for filtering results based on the start of a string attribute.
         * <p>Note: When the supplied value is null or an empty string, no predicate will be added to the builder.
         * The supplied value will be converted to uppercase using {@code String.toUpperCase()} </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .stringStartsWith("personName", model.getPersonNameFilter()); }
         * </pre>
         * In this example, the method is used to filter results where the "personName" attribute starts with the value provided by {@code model.getPersonNameFilter()}.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should start with. This method will perform a case-insensitive search.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder stringStartsWith(String attributeName, String value) {
            if (value != null && !value.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(attributeName)), "%" + value.toUpperCase()));
            }

            return this;
        }


        /**
         * Adds a predicate to the builder for matching strings that end with a specified value.
         * This method is case-insensitive and is useful for filtering results based on the end of a string attribute.
         * <p>Note: When the supplied value is null or an empty string, no predicate will be added to the builder.
         * The supplied value will be converted to uppercase using {@code String.toUpperCase()} </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .stringStartsWith("personName", model.getPersonNameFilter()); }
         * </pre>
         * In this example, the method is used to filter results where the "personName" attribute ends with the value provided by {@code model.getPersonNameFilter()}.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should start with. This method will perform a case-insensitive search.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder stringEndsWith(String attributeName, String value) {
            if (value != null && !value.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get(attributeName)), value.toUpperCase() + "%"));
            }

            return this;
        }


        /**
         * Adds a predicate to the builder for matching attributes that are equal to a specified value.
         * This method is useful for filtering results based on exact matches of an attribute's value.
         * <p>Note: When the supplied value is null, no predicate will be added to the builder. </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .equalsValue("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is exactly equal to 30.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should exactly match. This method supports matching against any type of object.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder equalsValue(String attributeName, Object value) {
            if (value != null) {
                predicates.add(criteriaBuilder.equal(root.get(attributeName), value));
            }

            return this;
        }


        /**
         * Adds a predicate to the builder for matching attributes that are equal to a specified value.
         * This method is useful for filtering results based on exact matches of an attribute's value.
         * <p>Note: When the supplied value is null or equal to 0, no predicate will be added to the builder.</p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .equalsValue("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is exactly equal to 30.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should exactly match. This method supports matching against any type of object.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder equalsValue(String attributeName, Long value) {
            if (value != null && value != 0) {
                predicates.add(criteriaBuilder.equal(root.get(attributeName), value));
            }

            return this;
        }


        /**
         * Adds a predicate to the builder for matching attributes that are in a specified collection of values.
         * This method is useful for filtering results based on whether an attribute's value is among a set of specified values.
         * <p>Note: When the supplied values is null or an empty collection, no predicate will be added to the builder.</p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code List<String> names = Arrays.asList("John", "Jane", "Doe");
         *
         * PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *                  .inCollection("name", names);}
         * </pre>
         * In this example, the method is used to filter results where the "name" attribute is in the list of names provided.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param values        A collection of values that the attribute should match. This method supports matching against any type of object.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder inCollection(String attributeName, Collection<?> values) {
            if (values != null && !values.isEmpty()) {
                predicates.add(root.get(attributeName).in(values));
            }

            return this;
        }

        /**
         * Adds a predicate to the builder for matching attributes that are NOT in the specified collection of values.
         * This method is useful for filtering results based on whether an attribute's value is NOT among a set of specified values.
         * <p>Note: When the supplied values is null or an empty collection, no predicate will be added to the builder.</p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code List<String> names = Arrays.asList("John", "Jane", "Doe");
         *
         * PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *                  .notInCollection("name", names);}
         * </pre>
         * In this example, the method is used to filter results where the "name" attribute is NOT in the list of names provided.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param values        A collection of values that the attribute should NOT match. This method supports matching against any type of object.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder notInCollection(String attributeName, Collection<?> values) {
            if (values != null && !values.isEmpty()) {
                predicates.add(root.get(attributeName).in(values).not());
            }

            return this;
        }

        /**
         * Adds a predicate to the builder for matching attributes that are less than or equal to a specified value.
         * This method is useful for filtering results based on a comparison of an attribute's value.
         * <p>Note: When the supplied value is null, no predicate will be added to the builder. </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .lessThanOrEqualTo("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is less than or equal to 30.
         *
         * @param <Y>           The type of the attribute value, which must extend Comparable.
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should be less than or equal to.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public <Y extends Comparable<? super Y>> PredicateBuilder lessThanOrEqualTo(String attributeName, Y value) {
            if (value != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(attributeName), value));
            }

            return this;
        }


        /**
         * Adds a predicate to the builder for matching attributes that are less than a specified value.
         * This method is useful for filtering results based on a comparison of an attribute's value.
         * <p>Note: When the supplied value is null, no predicate will be added to the builder. </p>
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .lessThan("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is less than 30.
         *
         * @param <Y>           The type of the attribute value, which must extend Comparable.
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should be less than.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public <Y extends Comparable<? super Y>> PredicateBuilder lessThan(String attributeName, Y value) {
            if (value != null) {
                predicates.add(criteriaBuilder.lessThan(root.get(attributeName), value));
            }

            return this;
        }

        /**
         * Adds a predicate to the builder for matching attributes that are greater than a specified value.
         * This method is useful for filtering results based on a comparison of an attribute's value.
         * <p>Note: When the supplied value is null, no predicate will be added to the builder. </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .greaterThan("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is greater than 30.
         *
         * @param <Y>           The type of the attribute value, which must extend Comparable.
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should be greater than.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public <Y extends Comparable<? super Y>> PredicateBuilder greaterThan(String attributeName, Y value) {
            if (value != null) {
                predicates.add(criteriaBuilder.greaterThan(root.get(attributeName), value));
            }

            return this;
        }

        /**
         * Adds a predicate to the builder for matching attributes that are greater than or equal to a specified value.
         * This method is useful for filtering results based on a comparison of an attribute's value.
         * <p>Note: When the supplied value is null, no predicate will be added to the builder. </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .greaterThanOrEqualTo("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is greater than or equal to 30.
         *
         * @param <Y>           The type of the attribute value, which must extend Comparable.
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should be greater than or equal to.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public <Y extends Comparable<? super Y>> PredicateBuilder greaterThanOrEqualTo(String attributeName, Y value) {
            if (value != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(attributeName), value));
            }

            return this;
        }

        /**
         * Adds a predicate to the builder for matching attributes that are between two specified values.
         * This method is useful for filtering results based on a range of values for an attribute.
         *
         * <p>Note: When both of the supplied values is NOT null, the method will add a predicate that checks
         * whether the attribute value is between the provided values.</p>
         * <p>Note: When ONLY the firstValue is not null, the method will add a predicate that checks
         * whether the attribute value is greater than or equal to the firstValue.</p>
         * <p>Note: When ONLY the secondValue is not null, the method will add a predicate that checks
         * whether the attribute value is less than or equal to the secondValue.</p>
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *               .between("age", 20, 30);
         * }</pre>
         * <p>
         * In this example, the method is used to filter results where the "age" attribute is between 20 and 30.
         *
         * @param <Y>           The type of the attribute value, which must extend Comparable.
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param firstValue    The lower bound of the range (inclusive).
         * @param secondValue   The upper bound of the range (inclusive).
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public <Y extends Comparable<? super Y>> PredicateBuilder between(String attributeName, Y firstValue, Y secondValue) {
            if (firstValue != null && secondValue != null) {
                predicates.add(criteriaBuilder.between(root.get(attributeName), firstValue, secondValue));
            } else if (firstValue != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(attributeName), firstValue));
            } else if (secondValue != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(attributeName), secondValue));
            }

            return this;
        }

        /**
         * Adds a predicate to the builder that combines multiple predicates using the logical AND operator.
         * This method is useful for applying multiple conditions that must all be true.
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code Predicate agePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("age"), 20);
         * Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%John%");
         * PredicateFactory.initializeBuilder(root, criteriaBuilder)
         *              .and(agePredicate, namePredicate); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is greater than or equal to 20 and the "name" attribute contains "John".
         *
         * @param restrictions An array of Predicate objects representing the conditions to be combined.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder and(Predicate... restrictions) {
            predicates.add(criteriaBuilder.and(restrictions));

            return this;
        }

        /**
         * Adds a predicate to the builder that combines multiple predicates using the logical OR operator.
         * This method is useful for applying multiple conditions where at least one must be true.
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code Predicate agePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("age"), 20);
         * Predicate namePredicate = criteriaBuilder.like(root.get("name"), "%John%");
         * PredicateFactory.initializeBuilder(root, criteriaBuilder)
         *              .or(agePredicate, namePredicate); }
         * </pre>
         * In this example, the method is used to filter results where either the "age" attribute is greater than or equal to 20 or the "name" attribute contains "John".
         *
         * @param restrictions An array of Predicate objects representing the conditions to be combined.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder or(Predicate... restrictions) {
            predicates.add(criteriaBuilder.or(restrictions));

            return this;
        }

        /**
         * Applies a custom predicate to the builder using a Consumer.
         * This method allows for the application of complex or custom predicates that cannot be directly expressed through the provided methods.
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateFactory.initializeBuilder(root, criteriaBuilder)
         *              .applyCustomPredicate(predicates -> predicates.add(criteriaBuilder.equal(root.get("age"), 20))); }
         * </pre>
         * In this example, the method is used to add a custom predicate that checks if an "age" attribute is equal to 20.
         *
         * @param consumer A Consumer that accepts a List of Predicate objects and modifies it as needed.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder applyCustomPredicate(Consumer<List<Predicate>> consumer) {
            consumer.accept(predicates);

            return this;
        }

        /**
         * Applies a custom predicate to the builder using a Consumer, but only if a specified condition is not null and true.
         * This method allows for conditional application of complex or custom predicates.
         *
         * @param condition A boolean value indicating whether the custom predicate should be applied.
         * @param consumer  A Consumer that accepts a List of Predicate objects and modifies it as needed.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code boolean shouldApplyCustomPredicate = true;
         * PredicateFactory.initializeBuilder(root, criteriaBuilder)
         *              .applyCustomPredicateIf(shouldApplyCustomPredicate, predicates -> predicates.add(criteriaBuilder.equal(root.get("age"), 20))); }
         * </pre>
         * In this example, the method is used to conditionally add a custom predicate that checks if an "age" attribute is equal to 20, based on the value of {@code shouldApplyCustomPredicate}.
         */
        public PredicateBuilder applyCustomPredicateIf(Boolean condition, Consumer<List<Predicate>> consumer) {
            if (condition != null && condition) consumer.accept(predicates);

            return this;
        }

        /**
         * Adds a predicate to the builder for matching attributes that are equal to a specified value.
         * This method is case-insensitive and trims any white spaces and is useful for filtering results based on exact matches of an attribute's value.
         * <p>Note: When the supplied value is null, no predicate will be added to the builder. </p>
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code PredicateUtil.initializeBuilder(root, criteriaBuilder)
         *              .equalsValue("age", 30); }
         * </pre>
         * In this example, the method is used to filter results where the "age" attribute is exactly equal to 30.
         *
         * @param attributeName The name of the attribute to be matched against. This should be the name of the field in the entity class.
         * @param value         The value that the attribute should exactly match. This method supports matching against any type of object.
         * @return The current PredicateBuilder instance, allowing for method chaining.
         */
        public PredicateBuilder equalsValue(String attributeName, String value) {
            if (value != null) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.trim(criteriaBuilder.upper(root.get(attributeName))), value.toUpperCase().trim()));
            }
            return this;
        }

        /**
         * Builds the final Predicate from the accumulated predicates.
         * This method combines all the predicates added to the builder using logical AND, resulting in a single Predicate that can be used in a query.
         *
         * <p>Example usage:</p>
         * <pre>
         * {@code Predicate predicate = PredicateFactory.initializeBuilder(root, criteriaBuilder)
         *                                   .applyCustomPredicate(predicates -> predicates.add(criteriaBuilder.equal(root.get("age"), 20)))
         *                                   .build(); }
         * </pre>
         * In this example, the method is used to build a Predicate after adding a custom predicate to the builder.
         *
         * @return A Predicate object representing the combined conditions.
         */
        public Predicate build() {
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }
    }

    /**
     * Initializes a new instance of PredicateBuilder with the specified root and criteria builder.
     * This method serves as the entry point for building predicates using the PredicateBuilder.
     * It is the only way to create a PredicateBuilder instance due to the private constructor of PredicateBuilder.
     *
     * <p>Example usage:</p>
     * <pre>
     * {@code Predicate predicate = PredicateFactory.initializeBuilder(root, criteriaBuilder)
     *          .applyCustomPredicate(predicates -> predicates.add(criteriaBuilder.equal(root.get("customField"), "customValue")))
     *          .build(); }
     * </pre>
     * In this example, the method is used to initialize a PredicateBuilder, add a custom predicate, and then build the final Predicate.
     *
     * @param root            The root of the query, representing the entity being queried.
     * @param criteriaBuilder The CriteriaBuilder instance used to construct the predicates.
     * @return A new instance of PredicateBuilder, ready for adding predicates.
     */
    public static PredicateBuilder initializeBuilder(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return new PredicateBuilder(root, criteriaBuilder);
    }
}
