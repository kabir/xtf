package cz.xtf.core.cleaner.filters;

import java.util.Set;

import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.fabric8.kubernetes.api.model.rbac.Subject;

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
 * https://docs.openshift.com/dedicated/3/admin_guide/manage_rbac.html
 *
 * However, there are some other occurrences which have a different roleRef, here the subject seems to be one of the following
 * two:
 * 
 * <pre>
 * subjects:
 * - apiGroup: rbac.authorization.k8s.io
 *   kind: Group
 *   name: system:serviceaccounts:dedicated-admin
 * </pre>
 * 
 * or
 * 
 * <pre>
 * subjects:
 * - apiGroup: rbac.authorization.k8s.io
 *   kind: Group
 *   name: dedicated-admins
 * </pre>
 *
 * The above subject entries show up both when the roleRef matches the above or not, so the subject seems to be the
 * best thing to filter on.
 */
public class DedicatedAdminCleanerFilter implements OpenShiftCleanerFilter {

    private static final Set<String> SUBJECT_NAMES = Set.of("dedicated-admins", "system:serviceaccounts:dedicated-admin");

    @Override
    public boolean filterRoleBinding(RoleBinding rb) {
        for (Subject subject : rb.getSubjects()) {
            String kind = subject.getKind();
            if (!kind.equals("Group")) {
                continue;
            }
            String apiGroup = subject.getApiGroup();
            if (!apiGroup.contains("rbac.authorization.k8s.io")) {
                continue;
            }
            String name = subject.getName();
            if (SUBJECT_NAMES.contains(name)) {
                return false;
            }
        }
        return true;
    }
}
