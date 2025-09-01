import Popup from "reactjs-popup";
import QRCode from "react-qr-code";
import { LucideX } from "lucide-react";
import { parser } from "./Parser";

/**
 * QR Popup component that displays a QR code for exporting the action sequence.
 * The QR code can be scanned to import the sequence into the app.
 * 
 * @returns {JSX.Element} - The QR Popup component.
 */

export const QrPopup = () => {
  return (
    <Popup
      trigger={
        <div className=" p-3 rounded-2xl bg-purple-800 cursor-pointer">
          <p className="text-white font-bold">Export</p>
        </div>
      }
      modal
      nested
      // Adding custom overlay and content styles to ensure the popup is on top
      overlayStyle={{
        zIndex: 9999, // Set a high z-index for the overlay
      }}
      contentStyle={{
        zIndex: 10000, // Set a higher z-index for the content
      }}
    >
      {((close: () => void) => (
        <div className="bg-slate-700 rounded-lg shadow-lg flex flex-col items-center p-6">
          <div className="" onClick={close}>
            <LucideX className="absolute top-2 right-2 cursor-pointer" />
          </div>
          <div className="bg-white rounded-lg shadow-lg flex flex-col items-center p-10 m-2">
            <p className="text-black mb-3 text-lg">
              Scan the code with the app to import the sequence.
            </p>
            <QRCode
              size={128}
              style={{ height: "auto", maxWidth: "100%", width: "100%" }}
              value={parser()}
              viewBox={`0 0 256 256`}
            />
          </div>
        </div>
      )) as unknown as React.ReactNode}
    </Popup>
  );
};
