package cz.xtf.core.cleaner.filters;

import io.fabric8.kubernetes.api.model.rbac.RoleBinding;

/**
 * On OpenShift sandbox we sometimes, but not always, some extra rolebindings appear in the namespace.
 * They have names like:
 * <ul>
 * <li>admin-dedicated-admins</li>
 * <li>admin-system:serviceaccounts:dedicated-admin</li>
 * <li>dedicated-admins-project-dedicated-admins</li>
 * <li>dedicated-admins-project-system:serviceaccounts:dedicated-admin</li>
 * </ul>
 *
 * Unfortunately none of them have labels, so we cannot use the label filtering mechanism to get rid of them. And
 * there might even be more.These are just the ones I noticed.
 *
 * Inspecting them, they all seem to contain the following value:
 * 
 * <pre>
 * roleRef:
 *  apiGroup: rbac.authorization.k8s.io
 *  kind: ClusterRole
 *  name: dedicated-admins-project
 * </pre>
 * 
 * Which is in line with what is mentioned in
 * https://docs.openshift.com/container-platform/4.8/support/gathering-cluster-data.html
 */
public class DedicatedAdminCleanerFilter implements OpenShiftCleanerFilter {
    @Override
    public boolean filterRoleBinding(RoleBinding rb) {
        return !rb.getRoleRef().getName().equals("dedicated-admins-project");
    }
}
