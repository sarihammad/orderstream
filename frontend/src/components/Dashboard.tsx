import React from 'react';

const Dashboard: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-100">
      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          <h1 className="text-3xl font-bold text-gray-900">
            OrderStream Dashboard
          </h1>
          <p className="mt-2 text-gray-600">
            Welcome to the OrderStream admin dashboard.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
