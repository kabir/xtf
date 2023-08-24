package cz.xtf.core.cleaner.filters;

import io.fabric8.kubernetes.api.model.rbac.RoleBinding;

/**
 * Interface to add more rules to filter out resources to be cleaned.
 * 
 * @see cz.xtf.core.openshift.OpenShift#SKIP_CLEAN_FILTER_CLASSES
 */
public interface OpenShiftCleanerFilter {
    /**
     * Checks whether a RoleBinding is provided by OpenShift or by us.
     *
     * @param rb the RoleBinding to inspect
     * @return {@code true} if the resource should be cleaned, {@code false} otherwise
     */
    default boolean filterRoleBinding(RoleBinding rb) {
        return true;
    }
}
