package pl.pokerquiz.pokerquiz.rest;

import java.util.List;

import pl.pokerquiz.pokerquiz.datamodel.rest.Category;

public interface OnCategoriesDownloadedListener {
    public void onCategoriesDownloaded(boolean success, List<Category> categories);
}
