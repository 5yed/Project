import styles from './Popup.module.css';

export default function Popup(props) {
  return (props.trigger) ? (
    <div className={styles.popup}>
      <div className={styles.popupContents}>
        {props.children}
        <div className={styles.popupButtonCol}>
          <button className={styles.popupCloseButton} onClick={() => props.setTrigger(false)}>
            Close
          </button>
          {props.submitButton}
        </div>
      </div>
    </div>
  ) : "";
}
