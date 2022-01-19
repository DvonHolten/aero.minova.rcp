package aero.minova.rcp.css.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.css.MinovaSectionStyler;

public class MinovaSection extends Section {
	private ICssStyler cssStyler;

	private final ImageHyperlink imageLink;

	private boolean expandable;

	public MinovaSection(Composite parent, int style) {
		super(parent, style);

		cssStyler = new MinovaSectionStyler(this);

		expandable = (style & ExpandableComposite.TWISTIE) != 0;
		this.imageLink = new ImageHyperlink(this, SWT.LEFT | getOrientation() | SWT.NO_FOCUS);
		this.getImageLink().setUnderlined(false);
		this.getImageLink().setBackground(getTitleBarGradientBackground());
		this.getImageLink().setForeground(getTitleBarForeground());
		this.getImageLink().setFont(parent.getFont());
		super.textLabel = this.getImageLink();

		this.getImageLink().addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				if (!isExpanded()) {
					setExpanded(true);
				} else if (MinovaSection.this.getExpandable()) {
					setExpanded(false);
				}
			}
		});
		this.imageLink.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				System.out.println("MouseUP" + e.count);

			}

			@Override
			public void mouseDown(MouseEvent e) {
				System.out.println("Mouse Down" + e.count);

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out.println("DoubleClick" + e.count);
			}

		});

	}

	public void setImage(final Image image) {
		if (image != null) {
			this.getImageLink().setImage(image);
		}
	}

	@Override
	public void setText(String title) {
		this.getImageLink().setText(title);
		this.getImageLink().requestLayout();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		((MinovaSectionData) this.getLayoutData()).visible = visible;
		this.getParent().requestLayout();
	}

	public boolean getExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	/**
	 * @return Style-Engine, der man die Properties geben kann
	 * @author Wilfried Saak
	 */
	public ICssStyler getCssStyler() {
		return cssStyler;
	}

	public ImageHyperlink getImageLink() {
		return imageLink;
	}
}
