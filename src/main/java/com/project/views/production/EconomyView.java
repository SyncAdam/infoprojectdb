package com.project.views.production;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;


import java.util.Arrays;
import java.util.List;


@Route("economy")
public class EconomyView extends VerticalLayout {

    public EconomyView() {
        // Expenses chart
        Chart expensesChart = createExpensesChart();
        add(expensesChart);

        // Profit/Loss chart
        Chart profitLossChart = createProfitLossChart();
        add(profitLossChart);
    }

    private Chart createExpensesChart() {
        Chart expensesChart = new Chart(ChartType.LINE);

        Configuration configuration = expensesChart.getConfiguration();
        configuration.setTitle("Expenses Over Time");
        
        List<Double> expensesData = Arrays.asList(1000.0, 1200.0, 900.0, 1500.0, 1300.0); // Sample data

        ListSeries expensesSeries = new ListSeries("Expenses", expensesData.toArray(new Number[0]));
        configuration.addSeries(expensesSeries);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May"); // Replace with actual time periods
        configuration.addxAxis(xAxis);

        return expensesChart;
    }

    private Chart createProfitLossChart() {
        Chart profitLossChart = new Chart(ChartType.LINE);

        Configuration configuration = profitLossChart.getConfiguration();
        configuration.setTitle("Profit/Loss Over Time");

        List<Double> profitLossData = Arrays.asList(500.0, 800.0, 300.0, 1200.0, 1000.0); // Sample data

        ListSeries profitLossSeries = new ListSeries("Profit/Loss", profitLossData.toArray(new Number[0]));
        configuration.addSeries(profitLossSeries);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May"); // Replace with actual time periods
        configuration.addxAxis(xAxis);

        return profitLossChart;
    }
} 

