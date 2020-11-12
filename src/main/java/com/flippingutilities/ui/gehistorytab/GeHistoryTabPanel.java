package com.flippingutilities.ui.gehistorytab;

import com.flippingutilities.OfferEvent;
import com.flippingutilities.ui.utilities.UIUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

/**
 * The panel that holds a view of the items in the trade history tab in the ge. This is so that users can manually add
 * trades they did while not using runelite (those trades will be in the GE trade history tab, assuming they are complete
 * and not too long ago).
 */
public class GeHistoryTabPanel extends JPanel
{
	public JPanel geHistoryTabOffersPanel;
	public JLabel statusTextLabel;
	public Set<Integer> selectedOffers;
	public List<OfferEvent> offersFromHistoryTab;
	public JButton addOffersButton;
	public Widget[] geHistoryTabWidgets;
	private static final int ORIGINAL_WIDGET_COLOR = 16750623;

	public GeHistoryTabPanel() {
		geHistoryTabOffersPanel = new JPanel();
		geHistoryTabOffersPanel.setBorder((new EmptyBorder(0, 0, 0, 6)));
		geHistoryTabOffersPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());
		add(createTitlePanel(), BorderLayout.NORTH);
		add(createOfferContainer(), BorderLayout.CENTER);
	}

	private JPanel createTitlePanel() {
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		titlePanel.setBorder(new EmptyBorder(5,5,5,5));
		JLabel titleText = new JLabel("Grand Exchange History", SwingConstants.CENTER);
		titleText.setFont(new Font("Verdana", Font.BOLD, 15));
		titlePanel.add(titleText, BorderLayout.CENTER);


		JPanel statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(new EmptyBorder(7,0,0,0));
		statusPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		statusTextLabel = new JLabel("0 items selected", SwingConstants.CENTER);
		statusPanel.add(statusTextLabel, BorderLayout.NORTH);
		statusTextLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
		statusTextLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		statusTextLabel.setFont(FontManager.getRunescapeBoldFont());

		addOffersButton = new JButton("Add selected offers");
		addOffersButton.setFont(FontManager.getRunescapeBoldFont());
		addOffersButton.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
		addOffersButton.setFocusPainted(false);
		addOffersButton.setVisible(false);

		statusPanel.add(addOffersButton, BorderLayout.CENTER);

		titlePanel.add(statusPanel, BorderLayout.SOUTH);
 		return titlePanel;
	}

	private JPanel createOfferContainer() {
		JPanel geHistoryTabOfferContainer = new JPanel(new BorderLayout());
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wrapper.add(geHistoryTabOffersPanel, BorderLayout.NORTH);

		JScrollPane scrollWrapper = new JScrollPane(wrapper);
		scrollWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);
		scrollWrapper.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
		scrollWrapper.getVerticalScrollBar().setBorder(new EmptyBorder(0, 0, 0, 0));

		geHistoryTabOfferContainer.add(scrollWrapper, BorderLayout.CENTER);
		geHistoryTabOfferContainer.setBorder(new EmptyBorder(5, 0, 0, 0));
		geHistoryTabOfferContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		return geHistoryTabOfferContainer;
	}

	private void onCheckBoxChange(int offerId, boolean selected) {
		int offset = offerId * 6;
		if (selected) {
			selectedOffers.add(offerId);
			geHistoryTabWidgets[offset + 2].setTextColor(ColorScheme.GRAND_EXCHANGE_PRICE.getRGB());
			geHistoryTabWidgets[offset + 3].setTextColor(ColorScheme.GRAND_EXCHANGE_PRICE.getRGB());
			geHistoryTabWidgets[offset + 5].setTextColor(ColorScheme.GRAND_EXCHANGE_PRICE.getRGB());
		}
		else {
			if (selectedOffers.contains(offerId)) {
				selectedOffers.remove(offerId);
				geHistoryTabWidgets[offset + 2].setTextColor(ORIGINAL_WIDGET_COLOR);
				geHistoryTabWidgets[offset + 3].setTextColor(ORIGINAL_WIDGET_COLOR);
				geHistoryTabWidgets[offset + 5].setTextColor(ORIGINAL_WIDGET_COLOR);
			}
		}
		statusTextLabel.setText(selectedOffers.size() + " items selected");
		addOffersButton.setVisible(selectedOffers.size() > 0);
	}

	public void rebuild(List<OfferEvent> offers, Widget[] widgets) {
		offersFromHistoryTab = offers;
		selectedOffers = new HashSet<>();
		addOffersButton.setVisible(false);
		statusTextLabel.setText("0 items selected");
		geHistoryTabWidgets = widgets;

		SwingUtilities.invokeLater(() ->
		{
			geHistoryTabOffersPanel.removeAll();
			List<GeHistoryTabOfferPanel> offerPanels = new ArrayList<>();
			for (int i=0; i < offers.size();i++) {
				offerPanels.add(new GeHistoryTabOfferPanel(offers.get(i), i, this::onCheckBoxChange));
			}
			UIUtilities.stackPanelsVertically((List) offerPanels, geHistoryTabOffersPanel, 4);
			revalidate();
			repaint();
		});
	}
}
