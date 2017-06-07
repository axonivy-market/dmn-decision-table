package com.axonivy.ivy.process.element.rule.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import ch.ivyteam.ivy.project.IIvyProject;

public class RuleResolver
{
  private static final String RULES_FOLDER = "rules/";
  private IIvyProject ivyProject;
  
  public RuleResolver(IIvyProject ivyProject) {
    this.ivyProject = ivyProject;
  }

  public List<String> findAvailableRulenamespaces()
  {
    List<String> namespaces = new ArrayList<>();
    for (IIvyProject project : getAllProjects()) {
      IFolder rulesFolder = project.getProject().getFolder(RULES_FOLDER);
      if (rulesFolder.exists())
      {
        namespaces.addAll(resolveRuleNamespacesFromFolder(rulesFolder));
      }
    }
    return namespaces;
  }

  private List<IIvyProject> getAllProjects()
  {
    List<IIvyProject> projects = new ArrayList<>();
    projects.add(ivyProject);
    projects.addAll(ivyProject.getAllRequiredProjects());
    return projects;
  }

  private static List<String> resolveRuleNamespacesFromFolder(IFolder ruleFolder)
  {
    try
    {
      RuleResourceVisitor visitor = new RuleResourceVisitor();
      ruleFolder.accept(visitor);
      return visitor.ruleNamespaces;
    }
    catch (CoreException ex)
    {
      String message = "Failed to load rule resources in \'" + ruleFolder.getFullPath() + "\'.";
      throw new RuntimeException(message, ex);
    }
  }
  
  private static class RuleResourceVisitor implements IResourceVisitor
  {
    private final List<String> ruleNamespaces = new ArrayList<>();

    @Override
    public boolean visit(IResource resource) throws CoreException
    {
      if (resource instanceof IFile && isSupportedRuleResource(resource.getName()))
      {
        IFile ruleFile = (IFile) resource;
        ruleNamespaces.add(getNamespaceFromFile(ruleFile));
      }
      return true;
    }

    private static String getNamespaceFromFile(IFile file)
    {
      IPath relativePath = file.getProjectRelativePath();
      return relativePath.removeFirstSegments(1).removeLastSegments(1).toString().replace('/', '.');
    }
    
    private static boolean isSupportedRuleResource(String path)
    {
      return path.endsWith("drl") || path.endsWith("xls");
    }
  }
}
