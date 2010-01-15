package org.sonar.plugins.scmactivity;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.NavigationSection;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;

@NavigationSection(NavigationSection.RESOURCE)
@UserRole(UserRole.USER)
public class ScmActivityWidget extends AbstractRubyTemplate implements RubyRailsWidget {
  public String getId() {
    return "scmactivity-widget";
  }

  public String getTitle() {
    return "ScmActivity widget";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/scmactivity/scmActivityWidget.erb";
  }
}
