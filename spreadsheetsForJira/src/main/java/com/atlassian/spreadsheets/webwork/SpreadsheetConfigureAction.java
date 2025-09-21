package com.atlassian.spreadsheets.webwork;

import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.web.action.JiraWebActionSupport;

@SupportedMethods({RequestMethod.GET, RequestMethod.HEAD})
public class SpreadsheetConfigureAction extends JiraWebActionSupport {
    @Override
    public String doDefault() {
        return SUCCESS;
    }

    @Override
    public String doExecute() {
        return SUCCESS;
    }
}



